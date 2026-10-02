package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.model.AddModelRequest
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.model.ChatSourceList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ModelPickerViewModel : ViewModel(), KoinComponent {

    private val api: Api by inject()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sourceList = MutableStateFlow<ChatSourceList>(emptyList())
    val sourceList: StateFlow<ChatSourceList> = _sourceList.asStateFlow()

    private val _modelList = MutableStateFlow<ChatModelList>(emptyList())
    val modelList: StateFlow<ChatModelList> = _modelList.asStateFlow()

    private val _selectedSource = MutableStateFlow<ChatSource?>(null)
    val selectedSource: StateFlow<ChatSource?> = _selectedSource.asStateFlow()

    private val _selectedModel = MutableStateFlow<ChatModel?>(null)
    val selectedModel: StateFlow<ChatModel?> = _selectedModel.asStateFlow()

    private var savedModels: ChatModelList = emptyList()
    private var providerModelsJob: Job? = null

    fun loadModels() {
        providerModelsJob?.cancel()
        viewModelScope.launch {
            _state.value = State.Loading
            _selectedSource.value = null
            _selectedModel.value = null
            _modelList.value = emptyList()

            try {
                _sourceList.value = api.chatApi.getSources().first()
                savedModels = api.chatApi.getModels().first()
                _state.value = State.Ready
            } catch (error: Throwable) {
                _state.value = State.LoadError(error.message ?: "Unable to load provider models")
            }
        }
    }

    fun selectSource(source: ChatSource) {
        _selectedSource.value = source
        _selectedModel.value = null
        _modelList.value = emptyList()
        loadProviderModels(source)
    }

    fun retrySelectedSource() {
        _selectedSource.value?.let(::loadProviderModels)
    }

    private fun loadProviderModels(source: ChatSource) {
        providerModelsJob?.cancel()
        providerModelsJob = viewModelScope.launch {
            _state.value = State.LoadingModels
            try {
                val models = api.chatApi.getProviderModels(source.id).first()
                if (_selectedSource.value?.id == source.id) {
                    _modelList.value = models
                    _state.value = State.Ready
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (_selectedSource.value?.id == source.id) {
                    _state.value = State.ModelLoadError(
                        error.message ?: "Unable to load provider models",
                    )
                }
            }
        }
    }

    fun selectModel(model: ChatModel) {
        if (model.sourceID == _selectedSource.value?.id && !isModelSaved(model)) {
            _selectedModel.value = model
        }
    }

    fun isModelSaved(model: ChatModel): Boolean = isSavedModel(model, savedModels)

    fun addSelectedModel() {
        val model = _selectedModel.value ?: return
        if (isModelSaved(model) || _state.value is State.Saving) return

        viewModelScope.launch {
            _state.value = State.Saving
            try {
                val addedModel = api.chatApi.addModel(
                    AddModelRequest(sourceID = model.sourceID, name = model.name)
                ).first()
                savedModels = savedModels + addedModel
                _state.value = State.Success(addedModel)
            } catch (error: Throwable) {
                _state.value = State.SaveError(error.message ?: "Unable to add model")
            }
        }
    }

    fun consumeSaveError() {
        if (_state.value is State.SaveError) {
            _state.value = State.Ready
        }
    }

    sealed interface State {
        data object Loading : State
        data object LoadingModels : State
        data object Ready : State
        data object Saving : State
        data class Success(val model: ChatModel) : State
        data class LoadError(val message: String) : State
        data class ModelLoadError(val message: String) : State
        data class SaveError(val message: String) : State
    }
}

internal fun isSavedModel(model: ChatModel, savedModels: ChatModelList): Boolean =
    model.id != null || savedModels.any { saved ->
        saved.sourceID == model.sourceID && saved.name == model.name
    }
