package dev.goood.chat_client.model

import kotlinx.serialization.Serializable

@Serializable
data class TestResponse(
    val data: Data? = null,
    val status: Int
)

@Serializable
data class Data(
    val message: String? = null
)

@Serializable
data class MyData(
    val message: String,
    val details: String,
    val value: Int
)

typealias MyDataList = List<MyData>

@Serializable
data class MyOtherData(
    val message: String,
    val index: Int,
)

typealias MyOtherDataList = List<MyData>