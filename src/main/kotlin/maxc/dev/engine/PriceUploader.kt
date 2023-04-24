package maxc.dev.engine

import maxc.dev.db.AssetTable
import maxc.dev.provider.ticker.PriceTickerModel
import org.ktorm.database.Database
import org.ktorm.dsl.insert

class PriceUploader(private val database: Database) {
    /**
     * Uploads the price of an asset to the database
     */
    fun uploadPrice(priceTickerModel: PriceTickerModel) {
        database.insert(AssetTable) {
            set(it.name, priceTickerModel.symbol)
            set(it.price, priceTickerModel.price)
            set(it.timestamp, priceTickerModel.timestamp)
        }
    }
}