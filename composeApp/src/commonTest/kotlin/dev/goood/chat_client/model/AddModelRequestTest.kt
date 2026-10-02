package dev.goood.chat_client.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AddModelRequestTest {

    @Test
    fun serializesBackendFieldNames() {
        val request = AddModelRequest(
            sourceID = 1,
            name = "models/gemini-2.5-flash",
        )

        assertEquals(
            "{\"source_id\":1,\"name\":\"models/gemini-2.5-flash\"}",
            Json.encodeToString(request),
        )
    }
}
