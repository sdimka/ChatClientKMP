package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import dev.goood.chat_client.Const
import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.services.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.post
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.setBody
import io.ktor.http.contentType


class Api: KoinComponent {

    private val authService: AuthService by inject()

    private val ktorfit =
        ktorfit {
            baseUrl(Const.Network.API_ENDPOINT)
            httpClient(
                HttpClient {
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
                    install(Auth) {
                        bearer {
                            loadTokens {
                                val accessToken = authService.getBearerToken()
                                val refreshToken = "" // userManager.get().refreshToken

                                if (accessToken != null) {
                                    return@loadTokens BearerTokens(accessToken,refreshToken)
                                }
                                return@loadTokens null
                            }

                            refreshTokens {
                                val token = client.post(Const.Network.REFRESH_TOKEN_ENDPOINT) {
                                    markAsRefreshTokenRequest()
                                    contentType(ContentType.Application.Json)
                                    setBody(authService.getUser())
                                }.body<TokenReply>()

                                token.token?.let {
                                    authService.setBearerToken(it)
                                    BearerTokens(
                                        accessToken = it,
                                        refreshToken = ""
                                    )
                                }
                            }
                        }
                    }
                    install(SSE)
                }
            )
            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory()
            )

        }

    val authApi = ktorfit.createAuthApi()
    val testApi = ktorfit.createTestApi()
    val streamApi = StreamApi(ktorfit.httpClient, Const.Network.API_ENDPOINT)

    val chatApi = ktorfit.createChatApi()
    val filesApi = ktorfit.createFilesApi()
    val translateApi = ktorfit.createTranslateApi()

}