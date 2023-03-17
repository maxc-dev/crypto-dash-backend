package maxc.dev.provider.price

import kotlinx.serialization.Serializable

@Serializable
data class BinanceAveragePriceModelBase(val id: String, val status: Int, val result: BinanceAveragePriceModel) : PriceModelMapper {
    override fun toPriceModel() = PriceModel(result.price.toDouble(), System.currentTimeMillis())
}
