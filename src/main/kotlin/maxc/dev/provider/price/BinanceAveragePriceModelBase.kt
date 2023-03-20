package maxc.dev.provider.price

import kotlinx.serialization.Serializable
import maxc.dev.plugins.kafka.KafkaModel

@Serializable
data class BinanceAveragePriceModelBase(val id: String, val status: Int, val result: BinanceAveragePriceModel) : KafkaModel<PriceModel> {
    override fun encode() = PriceModel(result.price.toDouble(), System.currentTimeMillis()).encode()
}
