package dev.goood.chat_client.core.network


import dev.goood.chat_client.model.MyOtherData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class StreamApi(private val client: HttpClient, baseUrl: String) {

    private val url = Url(baseUrl)

    fun myOtherDataStream(): Flow<MyOtherData> {
        return flow {
            client.sse(scheme = url.protocol.name, host = url.host, port = url.port, path = "/api/streamRequest") {

                incoming.collect { event ->
                    val deserializedData = event.data?.let {
                        Json.decodeFromString<MyOtherData>(it)
                    }
                    if (deserializedData != null) {
                        emit(deserializedData)
                    }
                }

            }
        }
    }
}