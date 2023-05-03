package maxc.dev

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import maxc.dev.dao.AssetTable
import maxc.dev.dao.DatabaseConnector
import maxc.dev.engine.PriceChangeManager
import maxc.dev.engine.PriceUploader
import maxc.dev.io.websocket.api.configureWebSocketApiRoutes
import maxc.dev.plugins.kafka.KafkaTopicManager
import maxc.dev.plugins.kafka.KafkaCredentials
import maxc.dev.plugins.kafka.KafkaStreamListener
import maxc.dev.provider.base.BinanceProvider
import maxc.dev.provider.ticker.BinanceMarketMiniTickerAllBase
import maxc.dev.provider.ticker.PriceTickerModel
import maxc.dev.util.TimestampUtil
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main() {
    embeddedServer(Netty, port = 8000, host = "10.77.72.231", module = Application::module)
        .start(wait = true)
}

@OptIn(DelicateCoroutinesApi::class)
fun Application.module() {
    val binance = BinanceProvider()
    val symbol = "MiniTickerAll"

    val localServer = "192.168.85.1"

    val kafkaClient = KafkaAdminClient.create(mapOf(
        "bootstrap.servers" to "$localServer:9092",
        "metadata.max.age.ms" to "1000",
    ))

    val kafkaCredentials = KafkaCredentials(
        server = "$localServer:9092",
        refreshMillis = 1000,
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

    val connector = DatabaseConnector("db:5432/?user=postgres", "postgres", "12345678")

    transaction {
        SchemaUtils.create(AssetTable)
    }

    val uploader = PriceUploader()
    val priceChangeManager = PriceChangeManager()

    GlobalScope.launch {
        consumer.subscribe().collect {
            uploader.uploadPrice(it)
        }
    }

    GlobalScope.launch {
        priceChangeManager.start()
    }

    configureWebSocketApiRoutes("ws", priceChangeManager)
}