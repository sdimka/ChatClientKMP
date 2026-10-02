package dev.goood.chat_client.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.network.UploadEvent
import dev.goood.chat_client.core.network.toUserMessage
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.core.other.acceptedUploadExtensions
import dev.goood.chat_client.core.other.uploadValidationError
import dev.goood.chat_client.model.FileList
import dev.goood.chat_client.model.MFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FileDialogViewModelImpl: FileDialogViewModel(), KoinComponent {

    private val api: Api by inject()
    private var currentChatID: Int? = null
    private var providerName: String? = null
    private var chatJob: Job? = null

    private val _selectedFileName = MutableStateFlow<String?>(null)
    override val selectedFileName = _selectedFileName.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state = _state.asStateFlow()
    override var uploadState by mutableStateOf(UploadState())
        private set

    private val _fileList = MutableStateFlow<FileList>(emptyList())
    override val fileList = _fileList.asStateFlow()

    private val _acceptedExtensions = MutableStateFlow<Set<String>?>(null)
    override val acceptedExtensions = _acceptedExtensions.asStateFlow()

    private val _uploadedFiles = MutableSharedFlow<MFile>(extraBufferCapacity = 1)
    override val uploadedFiles = _uploadedFiles.asSharedFlow()

    private val _deleteFileDialogState = MutableStateFlow<MFile?>(null)
    override val deleteFileDialogState = _deleteFileDialogState.asStateFlow()

    override fun setCurrentChat(chatID: Int?) {
        currentChatID = chatID
        providerName = null
        _acceptedExtensions.value = null
        _selectedFileName.value = null
        _fileList.value = emptyList()
        uploadState = UploadState()

        chatJob?.cancel()
        if (chatID == null) {
            _state.value = State.Success
            return
        }

        _state.value = State.Loading
        chatJob = viewModelScope.launch {
            // The provider decides which files are accepted; if it can't be determined the server validates.
            val sourceName = try {
                api.chatApi.getChats().first().firstOrNull { it.id == chatID }?.source?.name
            } catch (error: CancellationException) {
                throw error
            } catch (_: Throwable) {
                null
            }
            providerName = sourceName
            val accepted = sourceName?.let(::acceptedUploadExtensions)
            _acceptedExtensions.value = accepted

            if (accepted != null && accepted.isEmpty()) {
                _state.value = State.Success
            } else {
                updateFileList(chatID)
            }
        }
    }

    override fun updateFileList(chatID: Int) {
        _state.value = State.Loading
        viewModelScope.launch {

            api.filesApi.getFiles(chatID)
                .catch {
                    _state.value = State.Error(it.toUserMessage())
                }
                .collect { fList ->
                    _fileList.value = fList.sortedBy { it.createdAt }
                    _state.value = State.Success
                }

        }

    }

    override fun uploadFile(file: PlatformFile) {
        val chatID = currentChatID
        if (chatID == null) {
            uploadState = uploadState.copy(errorMessage = "No chat selected")
            return
        }

        viewModelScope.launch {
            val fileName = file.name
            _selectedFileName.value = fileName

            val validationError = uploadValidationError(
                fileName = fileName,
                sizeBytes = file.size(),
                providerName = providerName,
                acceptedExtensions = _acceptedExtensions.value,
            )
            if (validationError != null) {
                uploadState = UploadState(errorMessage = validationError)
                return@launch
            }

            uploadState = UploadState(isUploading = true)
            try {
                val content = ShareFileModel(fileName = fileName, bytes = file.readBytes())
                api.streamApi.uploadFile(content, chatID).collect { event ->
                    when (event) {
                        is UploadEvent.Progress -> uploadState = uploadState.copy(
                            progress = event.bytesSent / event.totalBytes.toFloat()
                        )

                        is UploadEvent.Completed -> {
                            _fileList.update { files -> files.filterNot { it.id == event.file.id } + event.file }
                            _uploadedFiles.emit(event.file)
                            uploadState = UploadState(isUploadComplete = true)
                        }
                    }
                }
            } catch (error: CancellationException) {
                uploadState = UploadState(errorMessage = "The upload was cancelled.")
                throw error
            } catch (error: Throwable) {
                uploadState = UploadState(errorMessage = error.toUserMessage())
            }
        }
    }

    override fun deleteFile(fileID: String) {
        val chatID = currentChatID
        if (chatID == null) {
            uploadState = uploadState.copy(errorMessage = "No chat selected")
            return
        }
        viewModelScope.launch {
            _state.value = State.Loading
            api.filesApi.deleteFile(fileID, chatID)
                .catch {
                    _state.value = State.Error(it.toUserMessage())
                }
                .collect {
                    updateFileList(chatID)
                }
        }
    }

    override fun setFileDialogState(file: MFile?) {
        _deleteFileDialogState.value = file
    }

}
