package dev.goood.chat_client.core.other

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


//class FileReader(
//    private val context: Context,
//    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//
//    @OptIn(ExperimentalUuidApi::class)
//    suspend fun uriToFileInfo(contentUri: Uri): FileInfo {
//        return withContext(ioDispatcher) {
//            val bytes = context
//                .contentResolver
//                .openInputStream(contentUri)
//                ?.use { inputStream ->
//                    inputStream.readBytes()
//                } ?: byteArrayOf()
//
//            val fileName = Uuid.random().toString()
//            val mimeType = context.contentResolver.getType(contentUri) ?: ""
//
//            FileInfo(
//                name = fileName,
//                mimeType = mimeType,
//                bytes = bytes
//            )
//        }
//    }
//}
//
//class FileInfo(
//    val name: String,
//    val mimeType: String,
//    val bytes: ByteArray
//)