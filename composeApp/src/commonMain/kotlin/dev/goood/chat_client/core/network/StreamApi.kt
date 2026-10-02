package dev.goood.chat_client.core.network


import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.Chunk
import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageRequest
import dev.goood.chat_client.model.StreamError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import io.ktor.http.defaultForFileExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json


class StreamApi(private val client: HttpClient, baseUrl: String) {

    private val url = Url(baseUrl)
    private val fileUploadUrl = "${baseUrl.trimEnd('/')}/api/file"

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
            )
            {
                incoming.collect { event ->
                    val data = event.data ?: return@collect
                    when (event.event) {
                        "message" -> emit(
                            ReplyVariants.SavedRequest(jsonConverter.decodeFromString<Message>(data))
                        )

                        "chunk" -> emit(
                            ReplyVariants.Chunks(jsonConverter.decodeFromString<Chunk>(data))
                        )

                        "finalMessage" -> emit(
                            ReplyVariants.FinalReply(jsonConverter.decodeFromString<Message>(data))
                        )

                        "error" -> {
                            val error = runCatching { jsonConverter.decodeFromString<StreamError>(data) }
                                .getOrDefault(StreamError(details = data))
                            emit(
                                ReplyVariants.Error(
                                    message = error.userMessage(),
                                    partialResponse = error.partialResponse,
                                )
                            )
                        }

                        else -> println("Unknown SSE event: ${event.event}")
                    }
                }
            }
        }
    }

    fun uploadFile(content: ShareFileModel, chatID: Int): Flow<UploadEvent> = channelFlow {
        val response = client.submitFormWithBinaryData(
            url = fileUploadUrl,
            formData = formData {
                append("chat_id", chatID)
                append("file", content.bytes, Headers.build {
                    append(
                        HttpHeaders.ContentType,
                        ContentType.defaultForFileExtension(content.fileName.substringAfterLast('.', "")).toString()
                    )
                    append(HttpHeaders.ContentDisposition, "filename=${quoteHeaderValue(content.fileName)}")
                })
            }
        ) {
            onUpload { bytesSentTotal, totalBytes ->
                if (totalBytes != null && totalBytes > 0L) {
                    send(UploadEvent.Progress(bytesSentTotal, totalBytes))
                }
            }
        }
        send(UploadEvent.Completed(response.body<MFile>()))
    }
}

internal fun quoteHeaderValue(value: String): String =
    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

sealed interface ReplyVariants {
    data class SavedRequest(val content: Message) : ReplyVariants
    data class Chunks(val content: Chunk) : ReplyVariants
    data class FinalReply(val content: Message) : ReplyVariants
    data class Error(val message: String, val partialResponse: String? = null) : ReplyVariants
}

sealed interface UploadEvent {
    data class Progress(val bytesSent: Long, val totalBytes: Long) : UploadEvent
    data class Completed(val file: MFile) : UploadEvent
}
