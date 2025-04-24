package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import dev.goood.chat_client.model.TranslateData
import dev.goood.chat_client.model.TranslateResponse
import kotlinx.coroutines.flow.Flow

interface TranslateApi {

    @POST("/api/translate")
    fun translate(
        @Body message: TranslateData
    ) : Flow<TranslateResponse>
}