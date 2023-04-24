package maxc.dev.engine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import maxc.dev.db.Asset
import maxc.dev.db.AssetMapper.toPriceChangeModel
import maxc.dev.db.AssetTable
import org.ktorm.database.Database
import org.ktorm.dsl.*

/**
 * Periodically updates the price of an asset and updates the listener in the WebSocket API
 */
class PriceChangeManager(private val database: Database) {
    private val priceListener = mutableListOf<PriceChangeListener>()

    @Volatile
    var ticking = false

    fun addPriceChangeListener(listener: PriceChangeListener) {
        priceListener.add(listener)
    }

    /**
     * Starts the price change manager by periodically retrieving the price of an asset
     * and sending it to the listeners
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() = GlobalScope.launch {
        ticking = true
        while (ticking) {
            // gets the times and rounds to the nearest second
            var time = System.currentTimeMillis()
            val time1s = (time - 1000) / 1000
            val time1m = (time - 60000) / 1000
            val time30m = (time - 1800000) / 1000
            val time1h = (time - 3600000) / 1000
            val time12h = (time - 43200000) / 1000
            val time1d = (time - 86400000) / 1000
            time /= 1000

            // converts data to price model with percent changes
            val priceChangeModel = database.from(AssetTable).select()
                .where { AssetTable.timestamp inList listOf(time1s, time1m, time30m, time1h, time12h, time1d) }.orderBy(
                AssetTable.timestamp.desc()
            ).map {
                Asset(it[AssetTable.name]!!, it[AssetTable.price]!!, it[AssetTable.timestamp]!!)
            }.groupBy { AssetTable.name.desc() }.map { it.value.toPriceChangeModel() }

            // notifies listeners
            priceListener.forEach { it.onPricesChange(priceChangeModel) }

            // sleeps for a minute
            delay(1000L)
        }
    }
}