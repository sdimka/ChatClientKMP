package dev.goood.chat_client.core.network

import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import dev.goood.chat_client.services.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenAuthClientTest {

    private class FakeAuthService(var token: String?) : AuthService {
        var storedUser: User? = User("a@b.c", "secret")
        var expiredSessions = 0
        override fun getUser() = storedUser
        override fun setUser(user: User) { storedUser = user }
        override fun getBearerToken() = token
        override fun setBearerToken(token: String) { this.token = token }
        override fun login(user: User): Flow<TokenReply> = emptyFlow()
        override fun logout() { storedUser = null; token = null }
        override fun isAuthorized() = storedUser != null
        override val sessionExpired: SharedFlow<Unit> = MutableSharedFlow()
        override fun expireSession() { expiredSessions++; logout() }
    }

    private fun jwt(exp: Long): String {
        val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
        return "h.${encoder.encode("""{"exp":$exp}""".encodeToByteArray())}.s"
    }

    private val farFuture = jwt(4_000_000_000)
    private val expired = jwt(1)

    private fun client(
        authService: AuthService,
        login: suspend (User) -> String?,
        handler: (authorization: String?) -> Pair<HttpStatusCode, String>,
    ): Pair<HttpClient, MutableList<String?>> {
        val sentAuthHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            val auth = request.headers[HttpHeaders.Authorization]
            sentAuthHeaders += auth
            val (status, body) = handler(auth)
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = HttpClient(engine) { installApiAuthAndErrors(TokenManager(authService, login)) }
        return client to sentAuthHeaders
    }

    @Test
    fun attachesStoredToken() = runTest {
        val auth = FakeAuthService(farFuture)
        val (client, sent) = client(auth, login = { error("no login expected") }) { HttpStatusCode.OK to "[]" }

        client.get("http://test/api/GetChats")

        assertEquals(listOf<String?>("Bearer $farFuture"), sent)
    }

    @Test
    fun refreshesExpiringTokenBeforeSending() = runTest {
        val auth = FakeAuthService(expired)
        var logins = 0
        val (client, sent) = client(auth, login = { logins++; farFuture }) { HttpStatusCode.OK to "[]" }

        client.get("http://test/api/GetChats")

        assertEquals(1, logins)
        assertEquals(listOf<String?>("Bearer $farFuture"), sent)
        assertEquals(farFuture, auth.token)
    }

    @Test
    fun retriesOnceAfter401WithNewToken() = runTest {
        val serverRejected = jwt(4_000_000_001)
        val auth = FakeAuthService(serverRejected)
        val (client, sent) = client(auth, login = { farFuture }) { header ->
            if (header == "Bearer $farFuture") HttpStatusCode.OK to "ok" else HttpStatusCode.Unauthorized to ""
        }

        val body = client.get("http://test/api/GetChats").bodyAsText()

        assertEquals("ok", body)
        assertEquals(listOf<String?>("Bearer $serverRejected", "Bearer $farFuture"), sent)
    }

    @Test
    fun rejectedCredentialsEndTheSession() = runTest {
        val auth = FakeAuthService(expired)
        val (client, _) = client(auth, login = { throw ApiException(400, "Invalid credentials") }) {
            HttpStatusCode.Unauthorized to ""
        }

        val error = assertFailsWith<ApiException> { client.get("http://test/api/GetChats") }

        assertEquals(401, error.status)
        assertEquals(1, auth.expiredSessions)
        assertNull(auth.storedUser)
    }

    @Test
    fun errorBodyBecomesReadableException() = runTest {
        val auth = FakeAuthService(farFuture)
        val (client, _) = client(auth, login = { farFuture }) {
            HttpStatusCode.Conflict to """{"message":"Model already stored for this source"}"""
        }

        val error = assertFailsWith<ApiException> { client.get("http://test/api/Model/AddModel") }

        assertEquals(409, error.status)
        assertEquals("Model already stored for this source", error.message)
    }

    @Test
    fun concurrentRequestsShareOneRefresh() = runTest {
        val auth = FakeAuthService(expired)
        var logins = 0
        val (client, _) = client(auth, login = { logins++; farFuture }) { HttpStatusCode.OK to "[]" }

        List(5) { async { client.get("http://test/api/GetChats") } }.awaitAll()

        assertEquals(1, logins)
    }

    @Test
    fun authEndpointIsSentWithoutToken() = runTest {
        val auth = FakeAuthService(farFuture)
        val (client, sent) = client(auth, login = { farFuture }) { HttpStatusCode.OK to "{}" }

        client.get("http://test/api/get-auth-token")

        assertTrue(sent.single() == null)
    }
}
