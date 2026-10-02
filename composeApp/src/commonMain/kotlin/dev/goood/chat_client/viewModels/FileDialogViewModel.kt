package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.model.MFile
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


abstract class FileDialogViewModel: ViewModel() {

    abstract val state: StateFlow<State>
    abstract val uploadState: UploadState
    abstract val selectedFileName: StateFlow<String?>
    abstract val fileList: StateFlow<List<MFile>>

    /** Extensions the chat's provider accepts; null when unknown, empty when uploads aren't supported. */
    abstract val acceptedExtensions: StateFlow<Set<String>?>

    /** Emits each successfully uploaded file, so the dialog can attach it to the next message. */
    abstract val uploadedFiles: SharedFlow<MFile>

    abstract fun setCurrentChat(chatID: Int?)
    abstract fun updateFileList(chatID: Int)
    abstract fun uploadFile(file: PlatformFile)
    abstract fun deleteFile(fileID: String)
    abstract fun setFileDialogState(file: MFile?)
    abstract val deleteFileDialogState: StateFlow<MFile?>

    sealed interface State {
        data object Success: State
        data class Error(val message: String): State
        data object Loading: State

    }

}

data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)
