package dev.goood.chat_client.viewModels

import dev.goood.chat_client.model.ChatModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModelSearchTest {

    private fun model(name: String, displayName: String, description: String = "") = ChatModel(
        id = null,
        name = name,
        displayName = displayName,
        description = description,
        sourceID = 1,
    )

    private val flash = model("models/gemini-2.5-flash", "Gemini 2.5 Flash", "Fast and cheap")
    private val pro = model("models/gemini-2.5-pro", "Gemini 2.5 Pro", "Best for reasoning")
    private val gpt = model("gpt-4o", "GPT-4o", "Omni model with vision")
    private val embedding = model("models/text-embedding-004", "Text Embedding", "Embeddings for Gemini apps")
    private val models = listOf(flash, pro, gpt, embedding)

    private val nothingSaved: (ChatModel) -> Boolean = { false }

    @Test
    fun blankQueryReturnsAllModels() {
        assertEquals(models, filterModels(models, "   ", nothingSaved))
    }

    @Test
    fun blankQueryMovesSavedModelsToTheEnd() {
        val result = filterModels(models, "", isSaved = { it == flash })

        assertEquals(listOf(pro, gpt, embedding, flash), result)
    }

    @Test
    fun matchingIsCaseInsensitive() {
        assertEquals(listOf(gpt), filterModels(models, "gPt", nothingSaved))
    }

    @Test
    fun allTokensMustMatchInAnyOrder() {
        assertEquals(listOf(flash), filterModels(models, "flash gemini", nothingSaved))
    }

    @Test
    fun ignoresPunctuationDifferences() {
        assertEquals(listOf(gpt), filterModels(models, "gpt4o", nothingSaved))
    }

    @Test
    fun matchesTechnicalName() {
        assertEquals(listOf(embedding), filterModels(models, "004", nothingSaved))
    }

    @Test
    fun displayNameMatchesRankAboveDescriptionMatches() {
        val result = filterModels(models, "gemini", nothingSaved)

        assertEquals(listOf(flash, pro, embedding), result)
    }

    @Test
    fun displayNamePrefixRanksFirst() {
        val geminiInName = model("models/x", "Super Gemini")
        val result = filterModels(listOf(geminiInName, flash), "gemini", nothingSaved)

        assertEquals(listOf(flash, geminiInName), result)
    }

    @Test
    fun savedModelsAreListedAfterAvailableMatches() {
        val result = filterModels(models, "gemini 2.5", isSaved = { it == flash })

        assertEquals(listOf(pro, flash), result)
    }

    @Test
    fun noMatchReturnsEmptyList() {
        assertTrue(filterModels(models, "claude", nothingSaved).isEmpty())
    }

    @Test
    fun matchRangesFindsAllTokenOccurrences() {
        assertEquals(listOf(0..5, 11..15), matchRanges("Gemini 2.5 Flash", "flash gemini"))
    }

    @Test
    fun matchRangesMergesOverlappingRanges() {
        assertEquals(listOf(0..5), matchRanges("Gemini", "gem mini"))
    }

    @Test
    fun matchRangesIsEmptyForBlankQuery() {
        assertTrue(matchRanges("Gemini", " ").isEmpty())
    }
}
