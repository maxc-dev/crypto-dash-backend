package maxc.dev.provider.ticker

import kotlinx.serialization.Serializable
import maxc.dev.plugins.kafka.KafkaModel

@Serializable
data class BinanceMarketMiniTickerAllBase(
    val result: List<BinanceMarketMiniTickerAll>
) :  KafkaModel<PriceTickerModel> {
    override fun encode(): List<String> = result.map { it.asPriceTickerModel().encodedString() }
}