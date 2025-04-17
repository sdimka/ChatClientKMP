package dev.goood.chat_client.viewModels


import androidx.lifecycle.ViewModel
import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.MFile
import kotlinx.coroutines.flow.StateFlow

abstract class FileDialogViewModel: ViewModel() {

    abstract val uploadState: UploadState
    abstract val selectedFile: StateFlow<ShareFileModel?>
    abstract val fileList: StateFlow<List<MFile>>
    abstract fun setCurrentChat(chatID: Int?)
    abstract fun updateFileList(chatID: Int)
    abstract fun sendFile(sharedFile: ShareFileModel)

}

data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)