package dev.goood.chat_client.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StreamErrorTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesErrorEventWithPartialResponse() {
        val error = json.decodeFromString<StreamError>(
            """{"error":"provider_error","details":"Quota exceeded","partial_response":"Hello"}"""
        )
        assertEquals("Hello", error.partialResponse)
        assertEquals("provider_error: Quota exceeded", error.userMessage())
    }

    @Test
    fun hasFallbackMessage() {
        assertEquals("The reply could not be completed.", StreamError().userMessage())
    }

    @Test
    fun fileWithMissingFieldsStillDecodes() {
        val file = json.decodeFromString<MFile>("""{"id":"files/abc","filename":null,"bytes":null}""")
        assertEquals("files/abc", file.displayName)
        assertNull(file.createdAt)
    }

    @Test
    fun processingStatusIsDetected() {
        val file = json.decodeFromString<MFile>("""{"id":"f","status":"PROCESSING"}""")
        assertEquals(true, file.isProcessing)
    }
}
