package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Api {

    private val ktorfit =
        ktorfit {
            baseUrl("http://213.199.42.236:5000/")
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
                }
            )
            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory()
            )

        }

    val authApi = ktorfit.createAuthApi()
}