package dev.goood.chat_client.viewModels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.FileList
import dev.goood.chat_client.model.MFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.cancellation.CancellationException

class FileDialogViewModelImpl: FileDialogViewModel(), KoinComponent {

    private val api: Api by inject()
    private var currentChatID by mutableStateOf<Int?>(null)

    private val _selectedFile = MutableStateFlow<ShareFileModel?>(null)
    override val selectedFile = _selectedFile.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state = _state.asStateFlow()
    override var uploadState by mutableStateOf(UploadState())
        private set

    private val _fileList = MutableStateFlow<FileList>(emptyList())
    override val fileList = _fileList
        .onStart {
            currentChatID?.let { updateFileList(it) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    private val _deleteFileDialogState = MutableStateFlow<MFile?>(null)
    override val deleteFileDialogState = _deleteFileDialogState.asStateFlow()

    override fun setCurrentChat(chatID: Int?) {
        currentChatID = chatID
        _selectedFile.value = null
        uploadState = UploadState()
        if (chatID == null) {
            _fileList.value = emptyList()
        }
    }

    override fun updateFileList(chatID: Int) {
        _state.value = State.Loading
        viewModelScope.launch {

            api.filesApi.getFiles(chatID)
                .catch {
                    print(it)
                    _state.value = State.Error(it.message ?: "Unkown error")
                }
                .collect { fList ->
                    _fileList.value = fList.sortedBy { it.createdAt }
                    _state.value = State.Success
                }

        }

    }

    override fun uploadFile(sharedFile: ShareFileModel) {
        _selectedFile.value = sharedFile
        if (currentChatID == null) {
            uploadState = uploadState.copy(
                errorMessage = "No chat selected"
            )
            return
        }
        api.streamApi.uploadFile(sharedFile, currentChatID!!)
            .onStart {
                uploadState = uploadState.copy(
                    isUploading = true,
                    isUploadComplete = false,
                    errorMessage = null,
                    progress = 0f
                )
            }
            .onEach { progressUpdate ->
                uploadState = uploadState.copy(
                    progress = progressUpdate.bytesSent / progressUpdate.totalBytes.toFloat()
                )
            }
            .onCompletion { cause ->
                if (cause == null) {
                    uploadState = uploadState.copy(
                        isUploading = false,
                        isUploadComplete = true
                    )
                    updateFileList(currentChatID!!)
                } else if (cause is CancellationException) {
                    uploadState = uploadState.copy(
                        isUploading = false,
                        errorMessage = "The upload was cancelled!",
                        isUploadComplete = false,
                        progress = 0f
                    )
                }
            }
            .catch {
                print(it.message)
                uploadState = uploadState.copy(
                    isUploading = false,
                    errorMessage = it.message
                )
            }
            .launchIn(viewModelScope)
    }

    override fun deleteFile(fileID: String) {
        if (currentChatID == null) {
            uploadState = uploadState.copy(
                errorMessage = "No chat selected"
            )
            return
        }
        api.filesApi.deleteFile(fileID, currentChatID!!)
            .onStart {
                _state.value = State.Loading
            }
            .onCompletion {
                updateFileList(currentChatID!!)
            }
            .catch {
                print(it.message)
                _state.value = State.Error(it.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    override fun setFileDialogState(file: MFile?) {
        _deleteFileDialogState.value = file
    }

}

