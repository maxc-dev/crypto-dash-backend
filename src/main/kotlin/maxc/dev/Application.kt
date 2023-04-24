package maxc.dev

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import maxc.dev.db.DatabaseConnector
import maxc.dev.engine.PriceChange
import maxc.dev.engine.PriceChangeManager
import maxc.dev.engine.PriceUploader
import maxc.dev.plugins.kafka.KafkaTopicManager
import maxc.dev.plugins.kafka.KafkaCredentials
import maxc.dev.plugins.kafka.KafkaStreamListener
import maxc.dev.provider.base.BinanceProvider
import maxc.dev.provider.ticker.BinanceMarketMiniTickerAllBase
import maxc.dev.provider.ticker.PriceTickerModel
import org.apache.kafka.clients.admin.KafkaAdminClient
import maxc.dev.dao.DatabaseFactory
import java.io.File
import kotlin.text.StringBuilder

fun main() {
    embeddedServer(Netty, port = 8000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

@OptIn(DelicateCoroutinesApi::class)
fun Application.module() {
    val binance = BinanceProvider()
    val symbol = "MiniTickerAll"


    val kafkaClient = KafkaAdminClient.create(mapOf(
        "bootstrap.servers" to "$localServer:9092",
        "metadata.max.age.ms" to "1000",
    ))

    val kafkaCredentials = KafkaCredentials(
        server = "$localServer:9092",
        refreshMillis = 1000,
        topicManager = KafkaTopicManager(kafkaClient)
    )

    DatabaseFactory.init(environment.config)
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


    val connector = DatabaseConnector("", "", "")
    val uploader = PriceUploader(connector.database)
    val priceChangeManager = PriceChangeManager(connector.database)

    GlobalScope.launch {
        consumer.subscribe().collect {
            uploader.uploadPrice(it)
        }
    }

    GlobalScope.launch {
        priceChangeManager.start()
    }
}