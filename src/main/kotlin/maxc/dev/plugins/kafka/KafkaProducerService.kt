package maxc.dev.plugins.kafka

import kotlinx.serialization.KSerializer
import maxc.dev.plugins.io.websocket.WebSocketAdapter
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.logging.Logger

class KafkaProducerService<T : KafkaModel<T>>(
    endpoint: String,
    path: String,
    port: Int? = null,
    subscription: String,
    private val topic: String,
    serializer: KSerializer<*>,
    private val kafka: KafkaCredentials,
) {
    private val log: Logger = Logger.getLogger(KafkaProducerService::class.java.name)

    // create generic websocket adapter
    private val webSocketConsumer = WebSocketAdapter<T>(
        host = endpoint,
        path = path,
        port = port,
        subscription = subscription,
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
        webSocketConsumer.subscribe().collect {
            kafkaProducer.send(ProducerRecord(topic, it.encode()))
        }
    }
}