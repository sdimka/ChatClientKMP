package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.ChatModel
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelPickerViewModelTest {

    private val providerModel = ChatModel(
        id = null,
        name = "models/gemini-2.5-flash",
        displayName = "Gemini 2.5 Flash",
        description = "Fast Gemini model",
        sourceID = 1,
    )

    @Test
    fun matchesSavedModelBySourceAndName() {
        val savedModelWithDifferentId = providerModel.copy(id = 7)

        assertTrue(isSavedModel(providerModel, listOf(savedModelWithDifferentId)))
    }

    @Test
    fun treatsProviderModelWithDatabaseIdAsSaved() {
        val registeredProviderModel = providerModel.copy(id = 100)

        assertTrue(isSavedModel(registeredProviderModel, emptyList()))
    }

    @Test
    fun treatsProviderModelWithoutDatabaseIdAsUnsaved() {
        assertFalse(isSavedModel(providerModel, emptyList()))
    }

    @Test
    fun doesNotMatchSameNameFromDifferentSource() {
        val otherSource = providerModel.copy(id = 7, sourceID = 2)

        assertFalse(isSavedModel(providerModel, listOf(otherSource)))
    }

    @Test
    fun doesNotMatchDifferentModelFromSameSource() {
        val otherModel = providerModel.copy(id = 7, name = "models/gemini-2.5-pro")

        assertFalse(isSavedModel(providerModel, listOf(otherModel)))
    }
}
