package maxc.dev.plugins.io.websocket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger

class WebSocketAdapter<T>(
    private val host: String,
    private val path: String,
    private val subscription: String,
    private val serializer: KSerializer<*>,
    private val port: Int? = null,
    private val jsonMapper: ((String) -> String)? = null
) {
    private val log: Logger = Logger.getLogger(WebSocketAdapter::class.java.name)

    private val decoder = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Creates a flow of data from the web socket and serializes it
     */
    suspend fun subscribe(): Flow<T> = flow {
        log.info("Subscribing to $host/$path${if (port != null) ":$port" else ""}${if (subscription.isNotBlank()) " with subscription [$subscription]" else ""}")
        val client = HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        // open the websocket
        client.webSocket(method = HttpMethod.Get, host = host, path = path, port = port, request = {
            url.protocol = URLProtocol.WSS
        }) {
            if (subscription.isNotBlank()) send(subscription)
            incoming.consumeAsFlow().mapNotNull { it as? Frame.Text }.mapNotNull { it.readText() }.map { jsonMapper?.invoke(it) ?: it }
                .mapNotNull { serializeJson(it) }
                .collect { emit(it) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun serializeJson(json: String): T? = try {
        decoder.decodeFromString(serializer, json) as T?
    } catch (e: Exception) {
        log.log(Level.WARNING, "Error while serializing json: $json", e)
        null
    }
}