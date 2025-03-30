package dev.goood.chat_client.core.network


import de.jensklingenberg.ktorfit.http.POST
import dev.goood.chat_client.model.TestResponse
import kotlinx.coroutines.flow.Flow


interface TestApi {

    @POST("api/GetTestData")
    fun getTestData(): Flow<TestResponse>
}