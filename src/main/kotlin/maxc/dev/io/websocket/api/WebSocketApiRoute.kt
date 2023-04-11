package maxc.dev.io.websocket.api

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import maxc.dev.engine.PriceChange
import maxc.dev.engine.PriceChangeListener
import maxc.dev.engine.PriceChangeManager
import java.time.Duration
import java.util.logging.Logger


fun Application.configureWebSocketApiRoutes(route: String, delay: Long = 1000L, priceChangeManager: PriceChangeManager) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(30)
    }

    routing {
        // creates a new web socket route
        webSocket(route) {
            val priceChangeReceiver = PriceChangeReceiver(this)
            priceChangeManager.addPriceChangeListener(priceChangeReceiver)
        }
    }

}

class PriceChangeReceiver(private val webSocketServerSession: DefaultWebSocketServerSession) : PriceChangeListener {
    private val log: Logger = Logger.getLogger(PriceChangeReceiver::class.java.name)

    override suspend fun onPricesChange(data: List<PriceChange>) {
        webSocketServerSession.sendSerialized(data)
        log.info("Sent ${data.size} price changes to client")
    }
}