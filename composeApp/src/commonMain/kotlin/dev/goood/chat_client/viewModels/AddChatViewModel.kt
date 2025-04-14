package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.model.ChatSourceList
import dev.goood.chat_client.model.NewChat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class AddChatViewModel: ViewModel(), KoinComponent  {

    private val api: Api by inject()

    private var _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val _sourceList = MutableStateFlow<ChatSourceList>(listOf(
        ChatSource(0, "Gemini"),
        ChatSource(1, "OpenAI"),
        ChatSource(2, "DeepSeek"),
        ChatSource(3, "Test"),
    ))
    val sourceList: StateFlow<ChatSourceList> = _sourceList
    private var allModels = emptyList<ChatModel>()
    private val _modelList = MutableStateFlow<ChatModelList>(emptyList())
    val modelList: StateFlow<ChatModelList> = _modelList
    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName

    private val selectedSource = MutableStateFlow<ChatSource?>(null)
    private val _selectedModel = MutableStateFlow<ChatModel?>(null)
    val selectedModel = _selectedModel.asStateFlow()

    private fun getSources() {
        viewModelScope.launch {
            api.chatApi.getSources()
                .catch {

                }
                .collect{
                    _sourceList.value = it
                    getModels()
                }
        }
    }

    private fun getModels() {
        viewModelScope.launch {
            api.chatApi.getModels()
                .catch {

                }
                .collect{
                    allModels = it
                }

        }
    }

    fun upDate(){
        getSources()
    }

    fun setSelectedSource(source: ChatSource) {
        selectedSource.value = source
        _modelList.value = allModels.filter { it.sourceID == source.id }
        validateForm()
    }

    fun setSelectedModel(model: ChatModel) {
        _selectedModel.value = model
        validateForm()
    }

    fun setChatName(name: String) {
        _chatName.value = name
        validateForm()
    }

    private fun validateForm() {
        if (chatName.value.isNotEmpty() && selectedSource.value != null && _selectedModel.value != null) {
            _state.value = State.FormValid
        } else {
            _state.value = State.Error("Fill all fields")
        }
    }

    fun createNewChat() {
        _state.value = State.Loading
        viewModelScope.launch {
            val chat = NewChat(
                id = 1,
                name = chatName.value,
                sourceID = selectedSource.value!!.id,
                modelID = _selectedModel.value!!.id,
            )
            api.chatApi.addChat(chat)
                .catch {
                    println(it)
                    _state.value = State.Error(it.message ?: "Unknown error")
                }
                .collect {
                    println("Chat saved")
                    _state.value = State.Success
                }

        }
    }

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State
        data object FormValid: State
    }
}