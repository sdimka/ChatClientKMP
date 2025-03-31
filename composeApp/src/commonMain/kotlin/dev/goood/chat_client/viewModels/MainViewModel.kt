package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.remaining
import io.ktor.utils.io.exhausted
import io.ktor.utils.io.readRemaining
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel: ViewModel(), KoinComponent {

    private val service: Api by inject()
    val reqString = MutableStateFlow("")

    fun getData(): String {
        return "Data from ViewModel"
    }

    fun getApiData() {
        viewModelScope.launch {
            service.testApi.getTestData()
                .collect {
                    print(it)
                }
        }
    }

    fun getApiList() {
        viewModelScope.launch {
            service.testApi.getTestDataList()
                .catch {
                    print(it.message)
                }
                .collect {
                    print(it)
                }
        }
    }

    fun getStreamString() {
        val client = HttpClient()
        viewModelScope.launch {
            client.prepareGet("http://213.199.42.236:5000//api/streamRequest").execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining()
                    while (!packet.exhausted()) {
                        val bytes = packet.readByte()

                        println("Received ${bytes} bytes from ${httpResponse.contentLength()}")
                    }
                }
            }
//            service.testApi.getTestDataStream()
//                .execute { httpResponse ->
//                    val channel = httpResponse.bodyAsBytes()
//                    var count = 0L
//                    channel.
//                    while (!channel.exhausted()) {
//                        val chunk = channel.readRemaining()
//                        count += chunk.remaining
//
////                            chunk.transferTo(stream)
//                        println("Received $count bytes from ${httpResponse.contentLength()}")
//                    }
//
//                }
        }
    }

}