package dev.goood.chat_client.core.other

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UploadRulesTest {

    @Test
    fun recognizesProvidersBySourceName() {
        assertTrue("mp4" in acceptedUploadExtensions("Gemini")!!)
        assertTrue("gif" in acceptedUploadExtensions("OpenAI")!!)
        assertEquals(emptySet(), acceptedUploadExtensions("OpenAI Assistants"))
        assertNull(acceptedUploadExtensions("DeepSeek"))
    }

    @Test
    fun acceptsSupportedFile() {
        assertNull(uploadValidationError("Report.PDF", 1024, "Gemini", acceptedUploadExtensions("Gemini")))
    }

    @Test
    fun rejectsUnsupportedExtension() {
        val error = uploadValidationError("notes.docx", 1024, "OpenAI", acceptedUploadExtensions("OpenAI"))
        assertNotNull(error)
        assertTrue(error.contains(".docx"))
        assertTrue(error.contains("pdf"))
    }

    @Test
    fun rejectsEverythingWhenProviderHasNoFileSupport() {
        assertNotNull(uploadValidationError("a.pdf", 10, "Assistants", emptySet()))
    }

    @Test
    fun rejectsFilesOverTheLimit() {
        val error = uploadValidationError("video.mp4", MAX_UPLOAD_BYTES + 1, "Gemini", acceptedUploadExtensions("Gemini"))
        assertNotNull(error)
        assertTrue(error.contains("20 MB"))
    }

    @Test
    fun rejectsEmptyFiles() {
        assertNotNull(uploadValidationError("a.txt", 0, null, null))
    }

    @Test
    fun unknownProviderOnlyChecksSize() {
        assertNull(uploadValidationError("anything.xyz", 10, null, null))
    }
}
