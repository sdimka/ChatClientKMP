package dev.goood.chat_client.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProviderModelsResponseTest {

    @Test
    fun deserializesRegisteredAndUnregisteredProviderModels() {
        val response = """
            [
              {
                "id": 42,
                "name": "models/gemini-2.5-pro",
                "display_name": "Gemini 2.5 Pro",
                "description": "Registered model",
                "source_id": 2
              },
              {
                "id": null,
                "name": "models/gemini-2.5-flash",
                "display_name": "Gemini 2.5 Flash",
                "description": "Provider model",
                "source_id": 2
              }
            ]
        """.trimIndent()

        val models = Json.decodeFromString<ChatModelList>(response)

        assertEquals(42, models[0].id)
        assertNull(models[1].id)
    }
}
