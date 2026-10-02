package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import dev.goood.chat_client.Const
import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.services.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class Api: KoinComponent {

    private val authService: AuthService by inject()

    private val tokenManager = TokenManager(authService) { user ->
        httpClient.post(Const.Network.REFRESH_TOKEN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body<TokenReply>().token
    }

    private val httpClient: HttpClient = HttpClient {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            header("Content-Type", "application/json")
        }
        installApiAuthAndErrors(tokenManager)
        install(SSE)
    }

    private val ktorfit =
        ktorfit {
            baseUrl(Const.Network.API_ENDPOINT)
            httpClient(httpClient)
            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory()
            )
        }

    val authApi = ktorfit.createAuthApi()
    val streamApi = StreamApi(httpClient, Const.Network.API_ENDPOINT)

    val chatApi = ktorfit.createChatApi()
    val filesApi = ktorfit.createFilesApi()
    val translateApi = ktorfit.createTranslateApi()

}

/** Bearer auth with token refresh, and non-2xx responses mapped to [ApiException]. */
internal fun HttpClientConfig<*>.installApiAuthAndErrors(tokenManager: TokenManager) {
    // Non-2xx responses are turned into ApiException (with a readable message) instead.
    expectSuccess = false
    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                val body = runCatching { response.bodyAsText() }.getOrDefault("")
                throw ApiException(response.status.value, parseServerMessage(body))
            }
        }
    }
    install(tokenAuthPlugin(tokenManager))
}
