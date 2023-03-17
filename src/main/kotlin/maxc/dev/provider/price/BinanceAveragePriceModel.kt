package maxc.dev.provider.price

import kotlinx.serialization.Serializable

@Serializable
data class BinanceAveragePriceModel(val mins: Int, val price: String)