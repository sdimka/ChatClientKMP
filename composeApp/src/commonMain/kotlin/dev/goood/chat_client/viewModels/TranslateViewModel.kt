package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.Lang
import dev.goood.chat_client.model.TranslateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TranslateViewModel: ViewModel(), KoinComponent {

    private val api: Api by inject()

    private val _state = MutableStateFlow<TranslateState>(TranslateState.Success)
    val state = _state.asStateFlow()
    val sourceLanguage = MutableStateFlow(Lang.EN)
    val targetLanguage = MutableStateFlow(Lang.RU)
    val fromString = MutableStateFlow("")
    val resultString = MutableStateFlow("Result will be here")

    fun setInput(resStr: String) {
        fromString.value = resStr
    }

    fun translate() {
        val message = TranslateData(
            text = fromString.value,
            sourceLanguageCode = sourceLanguage.value,
            targetLanguageCode = targetLanguage.value
        )
        val js = Json.encodeToString(message)
        print(js)
        viewModelScope.launch {
            _state.value = TranslateState.Loading
            api.translateApi.translate(message)
                .catch {
                    _state.value = TranslateState.Error(it.message?: "Unknown error")
                }
                .collect { res ->
                    resultString.value = res.translatedText[0]
                    _state.value = TranslateState.Success
                }
        }
    }

    fun changeDirection() {
        if (sourceLanguage.value == Lang.EN) {
            sourceLanguage.value = Lang.RU
            targetLanguage.value = Lang.EN
        } else {
            sourceLanguage.value = Lang.EN
            targetLanguage.value = Lang.RU
        }
    }

    sealed interface TranslateState {
        data object Success: TranslateState
        data class Error(val mess: String): TranslateState
        data object Loading: TranslateState
    }
}