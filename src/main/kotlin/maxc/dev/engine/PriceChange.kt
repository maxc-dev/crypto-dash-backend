package maxc.dev.engine

import kotlinx.serialization.Serializable

@Serializable
data class PriceChange(
    val name: String, //BTCUSDT
    val price: Double, //30635.29
    val change1s: Double?,
    val change1m: Double?,
    val change30m: Double?,
    val change1h: Double?,
    val change12h: Double?,
    val change1d: Double?,
)
