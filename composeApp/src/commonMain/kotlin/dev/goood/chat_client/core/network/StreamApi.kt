package dev.goood.chat_client.core.network


import dev.goood.chat_client.model.Chunk
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageRequest
import dev.goood.chat_client.model.MyOtherData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.sse.TypedServerSentEvent
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class StreamApi(private val client: HttpClient, baseUrl: String) {

    private val url = Url(baseUrl)

    fun myOtherDataStream(): Flow<MyOtherData> {
        return flow {
            client.sse(
                scheme = url.protocol.name,
                host = url.host,
                port = url.port,
                path = "/api/streamRequest")
            {
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

    fun streamRequest(message: MessageRequest): Flow<Chunk> {
        return flow {

            client.sse(
                scheme = url.protocol.name,
                host = url.host,
                port = url.port,
                path = "/api/streamMessage",
                {
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(message))
                })
            {
                incoming.collect { event ->
                    val deserializedData = event.data?.let {
                        Json.decodeFromString<Chunk>(it)
                    }
                    if (deserializedData != null) {
                        emit(deserializedData)
                    }
                }

            }
        }
    }

    abstract class Variant

    data class Variant1(
        val variant1: String
    ): Variant()

    data class Variant2(
        val variant2: String

    ): Variant()


    fun streamRequestWithType(message: MessageRequest): Flow<Variant> {
        return flow {
            client.sse(
                "http:// localhost:8080/ sse",
                {
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(message))
                },
                deserialize = { typeInfo: TypeInfo, jsonString: String ->
                    val serializer =
                        Json.serializersModule.serializer(typeInfo.kotlinType!!)
                    Json.decodeFromString(serializer, jsonString)!!
                },
                )
            {
                incoming.collect { event: TypedServerSentEvent<String> ->
                    when (event.event) {
                        "customer" -> {
                            val customer: Variant1? = event.data?.let {
                                Json.decodeFromString<Variant1>(
                                    it
                                )
                            }
                            if (customer != null) {
                                emit(customer)
                            }
                        }

                        "product" -> {
                            val product: Variant2? =
                                event.data?.let { Json.decodeFromString<Variant2>(it) }
                            if (product != null) {
                                emit(product)
                            }
                        }
                    }
                }
            }
        }
    }
}