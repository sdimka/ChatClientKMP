package dev.goood.chat_client.core.network

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import dev.goood.chat_client.model.TokenReply
import dev.goood.chat_client.model.User
import kotlinx.coroutines.flow.Flow

interface AuthApi {

    @POST("api/get-auth-token")
    fun login(@Body user: User): Flow<TokenReply>
}