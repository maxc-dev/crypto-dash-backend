package maxc.dev.io.websocket.api

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import maxc.dev.engine.PriceChange
import maxc.dev.engine.PriceChangeListener
import maxc.dev.engine.PriceChangeManager
import maxc.dev.plugins.kafka.KafkaProducerService
import java.time.Duration
import java.util.logging.Logger


fun Application.configureWebSocketApiRoutes(route: String, priceChangeManager: PriceChangeManager) {
    val log: Logger = Logger.getLogger(Application::class.java.name)

    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(30)
    }

    routing {
        route(route) {
            // creates a new web socket route
            webSocket("/data") {
                val priceChangeReceiver = PriceChangeReceiver(this)
                priceChangeManager.addPriceChangeListener(priceChangeReceiver)

                // keep socket open, but of a hack to do it though
                while (true) {
                    outgoing.send(Frame.Text("Hello"))
                    async { delay(1_000) }.join()
                }
            }

        }
    }

}

class PriceChangeReceiver(private val webSocketServerSession: DefaultWebSocketServerSession) : PriceChangeListener {
    private val log: Logger = Logger.getLogger(this::class.java.name)

    override suspend fun onPricesChange(data: List<PriceChange>) {
        webSocketServerSession.sendSerialized(data)
        log.info("Sent ${data.size} price changes to client")
    }
}