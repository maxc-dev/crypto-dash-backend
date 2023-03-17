package maxc.dev.plugins.kafka

import kotlinx.serialization.KSerializer
import maxc.dev.plugins.io.websocket.WebSocketAdapter
import maxc.dev.provider.Provider
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.logging.Logger

class WebSocketKafkaAdapter<T : KafkaModel<T>>(
    provider: Provider,
    symbol: String,
    serializer: KSerializer<*>,
    private val kafka: KafkaCredentials,
) {
    private val log: Logger = Logger.getLogger(WebSocketKafkaAdapter::class.java.name)
    private val topic = KafkaTopicManager.getTopic(provider, symbol)

    // create generic websocket adapter
    private val webSocketConsumerService = WebSocketAdapter<T>(
        host = provider.endpoint,
        path = provider.path,
        subscription = provider.getSubscriptionForSymbol(symbol),
        serializer = serializer
    )

    // create kafka producer
    private val kafkaProducer = KafkaProducer<String, String>(
        mapOf(
            "bootstrap.servers" to kafka.server,
            "key.serializer" to StringSerializer::class.java.canonicalName,
            "value.serializer" to StringSerializer::class.java.canonicalName,
            "group.id" to "$topic-producer",
            "metadata.max.age.ms" to "1000",
        )
    )

    /**
     * Subscribes tp the external web socket and flows into the kafka producer
     */
    suspend fun subscribeToWebSocket() {
        kafka.topicManager.createTopicCreateIfAbsent(topic)
        log.info("Subscribing to the web socket and sending the data to the kafka producer for topic $topic")
        // flow the websocket into the kafka producer
        webSocketConsumerService.subscribe().collect {
            kafkaProducer.send(ProducerRecord(topic, it.encode()))
        }
    }
}