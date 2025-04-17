package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.goood.chat_client.model.FileList
import kotlinx.coroutines.flow.Flow

interface FilesApi {

    @GET("/api/files")
    fun getFiles(@Query("chat_id") sourceID: Int): Flow<FileList>
}