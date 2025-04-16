package dev.goood.chat_client.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goood.chat_client.core.network.Api
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.ChatSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.cancellation.CancellationException

class FileDialogViewModel: ViewModel(), KoinComponent {

    private val api: Api by inject()
    private val _selectedFile = MutableStateFlow<ShareFileModel?>(null)
    val selectedFile = _selectedFile.asStateFlow()
    var state by mutableStateOf(UploadState())
        private set

    var fileList = mutableListOf<ShareFileModel>()
        private set

    fun updateFileList(source: ChatSource) {
        api
    }

    fun sendFile(sharedFile: ShareFileModel) {
        _selectedFile.value = sharedFile

        api.streamApi.uploadFile(sharedFile)
            .onStart {
                state = state.copy(
                    isUploading = true,
                    isUploadComplete = false,
                    errorMessage = null,
                    progress = 0f
                )
            }
            .onEach { progressUpdate ->
                state = state.copy(
                    progress = progressUpdate.bytesSent / progressUpdate.totalBytes.toFloat()
                )
            }
            .onCompletion { cause ->
                if (cause == null) {
                    state = state.copy(
                        isUploading = false,
                        isUploadComplete = true
                    )
                } else if (cause is CancellationException) {
                    state = state.copy(
                        isUploading = false,
                        errorMessage = "The upload was cancelled!",
                        isUploadComplete = false,
                        progress = 0f
                    )
                }
            }
            .catch {
                print(it.message)
                state = state.copy(
                    isUploading = false,
                    errorMessage = it.message
                )
            }
            .launchIn(viewModelScope)

    }

}

data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)