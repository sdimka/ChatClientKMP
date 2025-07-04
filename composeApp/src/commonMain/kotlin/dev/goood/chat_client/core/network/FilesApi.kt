package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.goood.chat_client.model.FileList
import kotlinx.coroutines.flow.Flow

interface FilesApi {

    @GET("/api/files")
    fun getFiles(@Query("chat_id") sourceID: Int): Flow<FileList>

    @DELETE("/api/file")
    fun deleteFile(@Query("file_name") fileID: String, @Query("chat_id") chatID: Int): Flow<Unit>
}