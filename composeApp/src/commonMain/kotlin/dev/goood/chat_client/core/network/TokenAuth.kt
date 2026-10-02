package dev.goood.chat_client.core.network

import dev.goood.chat_client.model.User
import dev.goood.chat_client.services.AuthService
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal const val AUTH_TOKEN_PATH = "api/get-auth-token"

// Refresh this long before the JWT's `exp`, so requests never go out with an about-to-expire token.
private const val EXPIRY_MARGIN_SECONDS = 60L

/** Reads the `exp` claim (epoch seconds) from a JWT, or null if the token can't be decoded. */
internal fun jwtExpiresAt(token: String): Long? = runCatching {
    val payload = token.split(".")[1]
    val json = Base64.UrlSafe
        .withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
        .decode(payload)
        .decodeToString()
    Json.parseToJsonElement(json).jsonObject["exp"]?.jsonPrimitive?.longOrNull
}.getOrNull()

@OptIn(ExperimentalTime::class)
internal fun isTokenExpiring(token: String, nowEpochSeconds: Long = Clock.System.now().epochSeconds): Boolean {
    val expiresAt = jwtExpiresAt(token) ?: return false
    return expiresAt - EXPIRY_MARGIN_SECONDS <= nowEpochSeconds
}

/**
 * Keeps the bearer token fresh. The API has no refresh token, so a new JWT is obtained by logging in
 * again with the stored credentials. If those are rejected the session is ended via [AuthService.expireSession].
 */
internal class TokenManager(
    private val authService: AuthService,
    private val login: suspend (User) -> String?,
) {
    private val mutex = Mutex()

    suspend fun validToken(): String? {
        val token = authService.getBearerToken()
        if (token != null && !isTokenExpiring(token)) return token
        return refresh(staleToken = token)
    }

    /** Returns a new token, or one another request refreshed meanwhile; null when the session has ended. */
    suspend fun refresh(staleToken: String?): String? = mutex.withLock {
        val current = authService.getBearerToken()
        if (current != null && current != staleToken && !isTokenExpiring(current)) return current

        val user = authService.getUser() ?: return null
        val newToken = try {
            login(user)
        } catch (error: ApiException) {
            // Login answers 400 for wrong credentials: the stored password is no longer valid.
            if (error.status == 400 || error.status == 401) {
                authService.expireSession()
                return null
            }
            throw error
        }

        if (newToken == null) {
            authService.expireSession()
            return null
        }
        authService.setBearerToken(newToken)
        newToken
    }
}

/** Adds the bearer token to every request, refreshing it before expiry and once more on a 401. */
internal fun tokenAuthPlugin(tokenManager: TokenManager) = createClientPlugin("TokenAuth") {
    on(Send) { request ->
        if (request.url.pathSegments.joinToString("/").endsWith(AUTH_TOKEN_PATH)) return@on proceed(request)

        val token = tokenManager.validToken()
        token?.let { request.headers[HttpHeaders.Authorization] = "Bearer $it" }

        val call = proceed(request)
        if (call.response.status != HttpStatusCode.Unauthorized) return@on call

        val freshToken = tokenManager.refresh(staleToken = token) ?: return@on call
        request.headers[HttpHeaders.Authorization] = "Bearer $freshToken"
        proceed(request)
    }
}
