package maxc.dev

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import maxc.dev.plugins.kafka.KafkaTopicManager
import maxc.dev.plugins.kafka.KafkaCredentials
import maxc.dev.plugins.kafka.KafkaStreamListener
import maxc.dev.provider.base.BinanceProvider
import maxc.dev.provider.ticker.BinanceMarketMiniTickerAllBase
import maxc.dev.provider.ticker.PriceTickerModel
import org.apache.kafka.clients.admin.KafkaAdminClient

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val binance = BinanceProvider()
    val symbol = "MiniTickerAll"

    val kafkaClient = KafkaAdminClient.create(mapOf(
        "bootstrap.servers" to "localhost:9092",
        "metadata.max.age.ms" to "1000",
    ))

    val kafkaCredentials = KafkaCredentials(
        server = "localhost:9092",
        refreshMillis = 200,
        topicManager = KafkaTopicManager(kafkaClient)
    )

    val consumer = KafkaStreamListener(
        kafkaCredentials = kafkaCredentials,
        endpoint = binance.endpoint,
        port = binance.port,
        path = binance.path,
        subscription = "",
        topic = KafkaTopicManager.getTopic(binance, symbol),
        serializer = BinanceMarketMiniTickerAllBase.serializer(),
        mapper = PriceTickerModel.mapper
    )

    CoroutineScope(Dispatchers.IO).launch {
        consumer.subscribe().collect {
            println(it)
        }
    }
}
