package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
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
        reqString.value = ""
        viewModelScope.launch {
            service.streamApi.myOtherDataStream()
                .collect { res ->
                    reqString.value += res.message
                }
        }
    }
}