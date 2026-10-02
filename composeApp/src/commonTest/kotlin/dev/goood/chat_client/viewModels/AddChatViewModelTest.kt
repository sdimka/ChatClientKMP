package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddChatViewModelTest {

    private val gemini = ChatSource(1, "Gemini")
    private val openAi = ChatSource(2, "OpenAI")
    private val geminiModel = ChatModel(id = 10, name = "gemini", displayName = "Gemini", description = "", sourceID = 1)
    private val gptModel = ChatModel(id = 20, name = "gpt", displayName = "GPT", description = "", sourceID = 2)
    private val unregistered = ChatModel(id = null, name = "new", displayName = "New", description = "", sourceID = 1)

    @Test
    fun listsOnlyRegisteredModelsOfTheSource() {
        assertEquals(listOf(geminiModel), modelsForSource(listOf(geminiModel, gptModel, unregistered), gemini.id))
    }

    @Test
    fun modelMustBelongToSource() {
        assertTrue(isModelValidForSource(geminiModel, gemini))
        assertFalse(isModelValidForSource(gptModel, gemini))
        assertFalse(isModelValidForSource(unregistered, gemini))
        assertFalse(isModelValidForSource(geminiModel, null))
        assertFalse(isModelValidForSource(null, openAi))
    }
}
