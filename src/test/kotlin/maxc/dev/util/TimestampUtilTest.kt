package maxc.dev.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TimestampUtilTest {
    @Test
    fun testTimestampDelta() = runBlocking {
        val now = TimestampUtil.getTimestamp()
        delay(1000L)
        val next = TimestampUtil.getTimestampSince(1)
        assertEquals(now, next)
    }
}