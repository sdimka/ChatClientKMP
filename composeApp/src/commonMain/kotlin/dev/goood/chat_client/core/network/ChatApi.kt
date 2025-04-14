package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import dev.goood.chat_client.model.ChatModelList
import dev.goood.chat_client.model.ChatSourceList
import dev.goood.chat_client.model.MessageList
import dev.goood.chat_client.model.NewChat
import dev.goood.chat_client.model.ResultMessage
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.Flow

interface ChatApi {

    @GET("/api/GetChats")
    fun getChats(): Flow<ChatList>

    @POST("/api/NewChat")
    fun addChat(@Body chat: NewChat): Flow<Chat>

    @POST("/api/DeleteChat")
    fun deleteChat(@Body chat: Chat): Flow<ResultMessage>

    @GET("/api/Model/GetSources")
    fun getSources(): Flow<ChatSourceList>

    @GET("/api/Model/GetAvailableModels")
    fun getModels(): Flow<ChatModelList>

    @GET("/api/Messages")
    fun getMessages(@Query("chat_id") chatId: Int): Flow<MessageList>

    @GET("/api/system_messages")
    fun getSystemMessages(): Flow<SystemMessageList>

    @PUT("/api/system_message")
    fun updateSystemMessage(
        @Body message: SystemMessage
    ): Flow<SystemMessage>

    @POST("/api/system_message")
    fun createSystemMessage(
        @Body message: SystemMessage
    ): Flow<SystemMessage>

    @DELETE("/api/system_message/{system_message_id}")
    fun deleteSystemMessage(
        @Path("system_message_id") id: Int
    ): Flow<ResultMessage>

}