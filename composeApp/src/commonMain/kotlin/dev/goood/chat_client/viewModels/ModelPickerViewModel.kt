package dev.goood.chat_client.viewModels

import dev.goood.chat_client.core.network.toUserMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.network.ApiException
import dev.goood.chat_client.model.AddModelRequest
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.model.ChatSourceList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val savedModels = MutableStateFlow<ChatModelList>(emptyList())

    // Provider models matching the search query, best matches first and already added models last.
    val filteredModels: StateFlow<ChatModelList> =
        combine(_modelList, _searchQuery, savedModels) { models, query, saved ->
            filterModels(models, query) { isSavedModel(it, saved) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var providerModelsJob: Job? = null

    fun loadModels() {
        providerModelsJob?.cancel()
        viewModelScope.launch {
            _state.value = State.Loading
            _selectedSource.value = null
            _selectedModel.value = null
            _modelList.value = emptyList()
            _searchQuery.value = ""

            try {
                _sourceList.value = api.chatApi.getSources().first()
                savedModels.value = api.chatApi.getModels().first()
                _state.value = State.Ready
            } catch (error: Throwable) {
                _state.value = State.LoadError(error.toUserMessage())
            }
        }
    }

    fun selectSource(source: ChatSource) {
        _selectedSource.value = source
        _selectedModel.value = null
        _modelList.value = emptyList()
        _searchQuery.value = ""
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
                        error.toUserMessage(),
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

    fun isModelSaved(model: ChatModel): Boolean = isSavedModel(model, savedModels.value)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Keyboard navigation: moves the selection through the selectable search results.
    fun moveSelection(step: Int) {
        val selectable = currentFilteredModels().filterNot(::isModelSaved)
        if (selectable.isEmpty()) return

        val currentIndex = selectable.indexOf(_selectedModel.value)
        val nextIndex = when {
            currentIndex == -1 && step > 0 -> 0
            currentIndex == -1 -> selectable.lastIndex
            else -> (currentIndex + step).coerceIn(0, selectable.lastIndex)
        }
        selectModel(selectable[nextIndex])
    }

    // Enter in the search field: adds the visible selected model, or selects the only remaining match.
    fun submitSearch() {
        val filtered = currentFilteredModels()
        val selected = _selectedModel.value
        if (selected != null && selected in filtered) {
            addSelectedModel()
            return
        }
        filtered.filterNot(::isModelSaved).singleOrNull()?.let(::selectModel)
    }

    private fun currentFilteredModels(): ChatModelList =
        filterModels(_modelList.value, _searchQuery.value, ::isModelSaved)

    fun addSelectedModel() {
        val model = _selectedModel.value ?: return
        if (isModelSaved(model) || _state.value is State.Saving) return

        viewModelScope.launch {
            _state.value = State.Saving
            try {
                val addedModel = api.chatApi.addModel(
                    AddModelRequest(sourceID = model.sourceID, name = model.name)
                ).first()
                savedModels.value = savedModels.value + addedModel
                _state.value = State.Success(addedModel)
            } catch (error: CancellationException) {
                throw error
            } catch (error: ApiException) {
                if (error.status == 409) {
                    // Added elsewhere meanwhile: refresh so the model is shown as "Added".
                    _selectedModel.value = null
                    runCatching { savedModels.value = api.chatApi.getModels().first() }
                    _state.value = State.SaveError("“${model.displayName}” is already added.")
                } else {
                    _state.value = State.SaveError(error.toUserMessage())
                }
            } catch (error: Throwable) {
                _state.value = State.SaveError(error.toUserMessage())
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

private val nonAlphanumeric = Regex("[^a-z0-9]")
private val whitespace = Regex("\\s+")

internal fun searchTokens(query: String): List<String> =
    query.trim().lowercase().split(whitespace).filter { it.isNotEmpty() }

private fun String.normalized(): String = lowercase().replace(nonAlphanumeric, "")

// Case-insensitive; also ignores punctuation so "gpt4o" matches "gpt-4o".
private fun String.matchesToken(token: String): Boolean {
    if (lowercase().contains(token)) return true
    val normalizedToken = token.normalized()
    return normalizedToken.isNotEmpty() && normalized().contains(normalizedToken)
}

/**
 * Every query token must match the model's display name, technical name or description.
 * Results are ordered by relevance (display name prefix > display name > technical name > description),
 * with models that are already added moved to the end. Original order is kept within each group.
 */
internal fun filterModels(
    models: ChatModelList,
    query: String,
    isSaved: (ChatModel) -> Boolean,
): ChatModelList {
    val tokens = searchTokens(query)
    if (tokens.isEmpty()) return models.sortedBy { isSaved(it) }

    val phrase = tokens.joinToString(" ")
    return models
        .mapNotNull { model ->
            val inDisplayName = tokens.all { model.displayName.matchesToken(it) }
            val inNames = tokens.all { model.displayName.matchesToken(it) || model.name.matchesToken(it) }
            val inAnyField = tokens.all {
                model.displayName.matchesToken(it) || model.name.matchesToken(it) || model.description.matchesToken(it)
            }
            val score = when {
                model.displayName.lowercase().startsWith(phrase) -> 0
                inDisplayName -> 1
                inNames -> 2
                inAnyField -> 3
                else -> return@mapNotNull null
            }
            model to score
        }
        .sortedWith(compareBy({ isSaved(it.first) }, { it.second }))
        .map { it.first }
}

/** Ranges of [text] that literally match any of the query tokens, merged, for highlighting. */
internal fun matchRanges(text: String, query: String): List<IntRange> {
    val lowerText = text.lowercase()
    val ranges = searchTokens(query).flatMap { token ->
        generateSequence(lowerText.indexOf(token).takeIf { it >= 0 }) { previous ->
            lowerText.indexOf(token, previous + 1).takeIf { it >= 0 }
        }.map { start -> start until start + token.length }.toList()
    }.sortedBy { it.first }

    return ranges.fold(mutableListOf()) { merged, range ->
        val last = merged.lastOrNull()
        if (last != null && range.first <= last.last + 1) {
            merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
        } else {
            merged.add(range)
        }
        merged
    }
}
