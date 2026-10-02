package dev.goood.chat_client.viewModels


import dev.goood.chat_client.model.MFile
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    override val selectedFileName: StateFlow<String?> =
        MutableStateFlow(null)

    override val acceptedExtensions: StateFlow<Set<String>?> = MutableStateFlow(null)

    override val uploadedFiles: SharedFlow<MFile> = MutableSharedFlow()

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
                filename = "salesOverview1_some_very_long_file_name.pdf",
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

    override fun uploadFile(file: PlatformFile) {

    }

    override fun deleteFile(fileID: String) {

    }

    override fun setFileDialogState(file: MFile?) {

    }

    override val deleteFileDialogState: StateFlow<MFile?> = MutableStateFlow(null)

}