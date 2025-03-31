package dev.goood.chat_client.core.network


import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Streaming
import dev.goood.chat_client.model.MyDataList
import dev.goood.chat_client.model.MyOtherData
import dev.goood.chat_client.model.TestResponse
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow


interface TestApi {

    @POST("api/GetTestData")
    fun getTestData(): Flow<TestResponse>

    @POST("api/GetTestList")
    fun getTestDataList(): Flow<MyDataList>

    @Streaming
    @GET("/api/streamRequest")
    suspend fun getTestDataStream(): HttpStatement
}