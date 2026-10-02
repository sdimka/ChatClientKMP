package dev.goood.chat_client.core.network

import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenAuthTest {

    private fun jwt(payload: String): String {
        val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
        val header = encoder.encode("""{"alg":"HS256","typ":"JWT"}""".encodeToByteArray())
        return "$header.${encoder.encode(payload.encodeToByteArray())}.signature"
    }

    @Test
    fun readsExpiryClaim() {
        assertEquals(1_700_000_000L, jwtExpiresAt(jwt("""{"sub":"admin","exp":1700000000}""")))
    }

    @Test
    fun missingExpiryIsNull() {
        assertNull(jwtExpiresAt(jwt("""{"sub":"admin"}""")))
    }

    @Test
    fun malformedTokenIsNull() {
        assertNull(jwtExpiresAt("not-a-jwt"))
    }

    @Test
    fun tokenIsExpiringWithinMargin() {
        val token = jwt("""{"exp":1000}""")
        assertTrue(isTokenExpiring(token, nowEpochSeconds = 950))
        assertFalse(isTokenExpiring(token, nowEpochSeconds = 900))
    }

    @Test
    fun tokenWithoutExpiryIsNeverTreatedAsExpiring() {
        assertFalse(isTokenExpiring("opaque-token", nowEpochSeconds = Long.MAX_VALUE))
    }

    @Test
    fun quotesHeaderValues() {
        assertEquals("\"my \\\"report\\\".pdf\"", quoteHeaderValue("my \"report\".pdf"))
    }
}
