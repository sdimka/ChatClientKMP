package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel
import dev.goood.chat_client.core.other.ShareFileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FileDialogViewModel: ViewModel() {

    private val _selectedFile = MutableStateFlow<ShareFileModel?>(null)
    val selectedFile = _selectedFile.asStateFlow()

    fun shareFile(shareFileModel: ShareFileModel) {
        _selectedFile.value = shareFileModel
    }

}

data class UploadState(
    val isUploading: Boolean = false,
    val isUploadComplete: Boolean = false,
    val progress: Float = 0f,
    val errorMessage: String? = null
)