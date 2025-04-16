package dev.goood.chat_client.core.other

import androidx.compose.runtime.Composable


expect class ShareManager {

    fun shareText(text: String)

    suspend fun shareFile(file: ShareFileModel): Result<Unit>

    suspend fun openFileWithFileManager(): ShareFileModel?

}


@Composable
expect fun rememberShareManager(): ShareManager