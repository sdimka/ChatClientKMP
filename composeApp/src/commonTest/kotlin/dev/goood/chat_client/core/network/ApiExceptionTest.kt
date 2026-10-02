package dev.goood.chat_client.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiExceptionTest {

    @Test
    fun parsesMessageField() {
        assertEquals("Model already stored", parseServerMessage("""{"message":"Model already stored","status":409}"""))
    }

    @Test
    fun fallsBackToErrorField() {
        assertEquals("Unsupported file type", parseServerMessage("""{"error":"Unsupported file type"}"""))
    }

    @Test
    fun ignoresHtmlErrorPages() {
        assertNull(parseServerMessage("<html><body>413 Request Entity Too Large</body></html>"))
    }

    @Test
    fun ignoresJsonWithoutMessage() {
        assertNull(parseServerMessage("""{"status":500}"""))
    }

    @Test
    fun keepsShortPlainText() {
        assertEquals("Invalid credentials", parseServerMessage("Invalid credentials\n"))
    }

    @Test
    fun ignoresBlankBody() {
        assertNull(parseServerMessage("   "))
    }

    @Test
    fun unsupportedMediaTypePrefersServerMessage() {
        val message = userMessageFor(415, "Accepted extensions: pdf, png")
        assertEquals("Accepted extensions: pdf, png", message)
    }

    @Test
    fun payloadTooLargeMentionsLimit() {
        assertTrue(userMessageFor(413, null).contains("20 MB"))
    }

    @Test
    fun badGatewayIsReportedAsProviderOutage() {
        assertEquals(
            "The AI provider is unavailable right now. Please try again later.",
            userMessageFor(502, "upstream error"),
        )
    }

    @Test
    fun exceptionMessageIsUserFacing() {
        val error = ApiException(404, "The chat does not exist.")
        assertEquals("The chat does not exist.", error.toUserMessage())
    }
}
