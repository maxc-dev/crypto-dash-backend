package maxc.dev.dao

import maxc.dev.dao.AssetMapper.priceToPercent
import maxc.dev.util.TimestampUtil
import kotlin.test.Test

class AssetMapperTest {
    private val assets1 = with(TimestampUtil.getTimestamp()) {
        listOf(
            Asset("BTCUSDT", 100.0, this),
            Asset("BTCUSDT", 200.0, TimestampUtil.getTimestampSince(1, this)),
            Asset("BTCUSDT", 300.0, TimestampUtil.getTimestampSince(60, this)),
            Asset("BTCUSDT", 400.0, TimestampUtil.getTimestampSince(1800, this)),
            Asset("BTCUSDT", 500.0, TimestampUtil.getTimestampSince(3600, this)),
            Asset("BTCUSDT", 600.0, TimestampUtil.getTimestampSince(43200, this)),
            Asset("BTCUSDT", 700.0, TimestampUtil.getTimestampSince(86400, this)),
        )
    }

    private val assets2 = with(TimestampUtil.getTimestamp()) {
        listOf(
            Asset("BTCUSDT", 1000.0, this),
            Asset("BTCUSDT", 500.0, TimestampUtil.getTimestampSince(1, this)),
            Asset("BTCUSDT", 250.0, TimestampUtil.getTimestampSince(60, this)),
            Asset("BTCUSDT", 125.0, TimestampUtil.getTimestampSince(1800, this)),
            Asset("BTCUSDT", 62.5, TimestampUtil.getTimestampSince(3600, this)),
            Asset("BTCUSDT", 31.25, TimestampUtil.getTimestampSince(43200, this)),
            Asset("BTCUSDT", 15.625, TimestampUtil.getTimestampSince(86400, this)),
        )
    }

    @Test
    fun testPriceChangePercentagePositive() {
        val changes = priceToPercent(assets1)
        for (i in changes.indices) {
            assert(changes[i] == i.toDouble())
        }
    }

    @Test
    fun testPriceChangePercentageNegative() {
        val changes = priceToPercent(assets2)
        assert(changes[0] == 0.0)
        assert(changes[1] == -0.5)
        assert(changes[2] == -0.75)
        assert(changes[3] == -0.875)
        assert(changes[4] == -0.9375)
        assert(changes[5] == -0.96875)
        assert(changes[6] == -0.984375)
    }
}