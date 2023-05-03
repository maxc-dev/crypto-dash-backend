package maxc.dev.util

object TimestampUtil {
    fun getTimestamp(): Long = (System.currentTimeMillis() / 1000) % 86400

    fun getTimestampSince(delta: Long): Long {
        val time = getTimestamp() - delta
        return if (time < 0) 86400 + (time) else time
    }

    fun getTimestampSince(delta: Long, time: Long = getTimestamp()): Long {
        val diff = time - delta
        return if (diff < 0) 86400 + (diff) else diff
    }
}