package maxc.dev.engine

import maxc.dev.dao.AssetTable
import maxc.dev.provider.ticker.PriceTickerModel
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.ktorm.database.Database

class PriceUploader(private val database: Database) {
    /**
     * Uploads the price of an asset to the database
     */
    fun uploadPrice(priceTickerModel: PriceTickerModel) {
        transaction {
            AssetTable.insert {
                it[name] = priceTickerModel.symbol
                it[price] = priceTickerModel.price
                it[timestamp] = priceTickerModel.timestamp
            }
        }
    }
}