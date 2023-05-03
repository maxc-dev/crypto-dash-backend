package maxc.dev.provider.ticker

import maxc.dev.plugins.kafka.KafkaModel
import maxc.dev.util.TimestampUtil

open class PriceTickerModel(val symbol: String, val price: Double, val timestamp: Long) : KafkaModel<PriceTickerModel> {
    /**
     * Encodes the price model into a string
     * In the format of "price|timestamp"
     */
    override fun encode() = listOf(encodedString())
    fun encodedString() = listOf(symbol, price, timestamp).joinToString(separator)

    override fun toString() = "$symbol @ $$price [$timestamp]"

    companion object {
        private const val separator = "|"

        /**
         * Mapper for decoding the encoded string
         */
        val mapper: (String) -> PriceTickerModel =
            {
                val (symbol, price, timestamp) = it.split(separator)
                PriceTickerModel(symbol, price.toDouble(), TimestampUtil.getTimestamp())
            }
    }
}