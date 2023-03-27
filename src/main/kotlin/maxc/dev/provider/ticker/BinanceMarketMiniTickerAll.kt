package maxc.dev.provider.ticker

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class BinanceMarketMiniTickerAll @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("E") val timestamp: Long,
    @JsonNames("s") val symbol: String,
    @JsonNames("c") val price: String,
) {
    fun asPriceTickerModel() = PriceTickerModel(symbol, price.toDouble(), timestamp)
}