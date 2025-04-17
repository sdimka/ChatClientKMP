package dev.goood.chat_client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//"id": "file-abc123",
//"object": "file",
//"bytes": 175,
//"created_at": 1613677385,
//"filename": "salesOverview.pdf",
//"purpose": "assistants",

@Serializable
data class MFile (
    val id: String,
    @SerialName("object")
    val obj: String,
    val bytes: Int,
    @SerialName("created_at")
    val createdAt: Long,
    val filename: String,
    val purpose: String,
)

typealias FileList = List<MFile>