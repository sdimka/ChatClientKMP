package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.Lang
import dev.goood.chat_client.model.TranslateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TranslateViewModel: ViewModel(), KoinComponent {

    private val api: Api by inject()

    val direction = MutableStateFlow(0)
    val fromString = MutableStateFlow("")
    val toString = MutableStateFlow("")

    val state = MutableStateFlow<TranslateState?>(null)

    fun translate() {
        val data = TranslateData(
            text = fromString.value,
            sourceLanguageCode = Lang.EN,
            targetLanguageCode = Lang.RU
        )
        val js = Json.encodeToString(data)
        print(js)
//        viewModelScope.launch {
//
//        }
    }

    sealed interface TranslateState {
        data class Success(val token: String): TranslateState
        data class Error(val message: String): TranslateState
        data object Loading: TranslateState
    }
}