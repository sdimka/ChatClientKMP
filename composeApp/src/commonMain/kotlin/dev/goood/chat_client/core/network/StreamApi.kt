package dev.goood.chat_client.core.network


import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.Chunk
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageRequest
import dev.goood.chat_client.model.MyOtherData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.sse.TypedServerSentEvent
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
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

    private val jsonConverter = Json { ignoreUnknownKeys = true }

    fun streamRequestWithType(message: MessageRequest): Flow<ReplyVariants> {
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
                        "message" -> {
                            val msg: Message? = event.data?.let {
                                jsonConverter.decodeFromString<Message>(
                                    it
                                )
                            }
                            if (msg != null) {
                                emit(ReplyVariants.SavedRequest(msg))
                            }
                        }

                        "chunk" -> {
                            val chk: Chunk? =
                                event.data?.let {
                                    Json.decodeFromString<Chunk>(it)
                                }
                            if (chk != null) {
                                emit(ReplyVariants.Chunks(chk))
                            }
                        }

                        "finalMessage" -> {
                            val msg: Message? = event.data?.let {
                                jsonConverter.decodeFromString<Message>(
                                    it
                                )
                            }
                            if (msg != null) {
                                emit(ReplyVariants.FinalReply(msg))
                            }
                        }

                        else -> {
                            println(event)
                            println(event.event)
                        }
                    }
                }
            }
        }
    }

    fun uploadFile(content: ShareFileModel): Flow<ProgressUpdate> = channelFlow {
//        val info = fileReader.uriToFileInfo(contentUri)

        client.submitFormWithBinaryData(
            url = "https://dlptest.com/https-post/",
            formData = formData {
                append("description", "Test")
                append("the_file", content.bytes, Headers.build {
                    append(HttpHeaders.ContentType, content.mime.toString())
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=${content.fileName}"
                    )
                })
            }
        ) {
            onUpload { bytesSentTotal, totalBytes ->
                if (totalBytes != null) {
                    if(totalBytes > 0L) {
                        send(ProgressUpdate(bytesSentTotal, totalBytes))
                    }
                }
            }
        }
    }
}

sealed interface ReplyVariants {
    data class SavedRequest(val content: Message) : ReplyVariants
    data class Chunks(val content: Chunk) : ReplyVariants
    data class FinalReply(val content: Message) : ReplyVariants
    data class Error(val message: String) : ReplyVariants
}

data class ProgressUpdate(
    val bytesSent: Long,
    val totalBytes: Long
)