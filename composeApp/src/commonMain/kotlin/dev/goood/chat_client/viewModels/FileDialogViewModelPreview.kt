package dev.goood.chat_client.viewModels


import dev.goood.chat_client.core.other.ShareFileModel
import dev.goood.chat_client.model.MFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FileDialogViewModelPreview : FileDialogViewModel() {

    override val state: StateFlow<State> =
        MutableStateFlow(State.Success)
    
    override val uploadState: UploadState = UploadState(
        isUploading = false,
        isUploadComplete = false,
        progress = 0f,
        errorMessage = null
    )

    override val selectedFile: StateFlow<ShareFileModel?> =
        MutableStateFlow(null)

    override val fileList: StateFlow<List<MFile>> = MutableStateFlow(
        listOf(
            MFile(
                id = "1",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "2",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview1.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "3",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview2.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "1",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "2",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview1.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "3",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview2.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "1",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "2",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview1.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "3",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview2.pdf",
                purpose = "assistants"
            )
        )
    )

    override fun setCurrentChat(chatID: Int?) {

    }

    override fun updateFileList(chatID: Int) {

    }

    override fun uploadFile(sharedFile: ShareFileModel) {

    }

    override fun deleteFile(fileID: String) {

    }

    override fun setFileDialogState(file: MFile?) {

    }

    override val deleteFileDialogState: StateFlow<MFile?> = MutableStateFlow(null)

}