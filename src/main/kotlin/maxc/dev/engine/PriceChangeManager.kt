package maxc.dev.engine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import maxc.dev.dao.Asset
import maxc.dev.dao.AssetMapper.toPriceChangeModel
import maxc.dev.dao.AssetTable
import maxc.dev.util.TimestampUtil
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.logging.Logger

/**
 * Periodically updates the price of an asset and updates the listener in the WebSocket API
 */
class PriceChangeManager {
    private val log: Logger = Logger.getLogger(this::class.java.name)
    private val priceListener = mutableListOf<PriceChangeListener>()

    @Volatile
    var ticking = false

    fun addPriceChangeListener(listener: PriceChangeListener) {
        priceListener.add(listener)
        log.info("Added listener $listener")
    }

    /**
     * Starts the price change manager by periodically retrieving the price of an asset
     * and sending it to the listeners
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() = GlobalScope.launch {
        ticking = true
        while (ticking) {
            val start = System.currentTimeMillis()

            // gets the times and rounds to the nearest second
            val time = TimestampUtil.getTimestamp()
            val time1s = TimestampUtil.getTimestampSince(1, time)
            val time1m = TimestampUtil.getTimestampSince(60, time)
            val time30m = TimestampUtil.getTimestampSince(1800, time)
            val time1h = TimestampUtil.getTimestampSince(3600, time)
            val time12h = TimestampUtil.getTimestampSince(43200, time)
            val time1d = TimestampUtil.getTimestampSince(86400, time)

            val priceChangeModel = transaction {
                // converts data to price model with percent changes
                val result = AssetTable.select { AssetTable.timestamp inList listOf(time, time1s, time1m, time30m, time1h, time12h, time1d) }.orderBy(
                    AssetTable.timestamp to SortOrder.ASC
                ).map {
                    Asset(it[AssetTable.name], it[AssetTable.price], it[AssetTable.timestamp])
                }.groupBy { AssetTable.name to SortOrder.DESC }.map { it.value.toPriceChangeModel() }
                result
            }

            // notifies listeners
            priceListener.forEach { it.onPricesChange(priceChangeModel) }

            // sleeps for a minute
            val end = System.currentTimeMillis()
            val sleep = 1000L - (end - start)
            if (sleep > 0) delay(sleep)
        }
    }
}