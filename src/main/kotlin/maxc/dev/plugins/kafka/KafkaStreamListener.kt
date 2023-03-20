package maxc.dev.plugins.kafka

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

/**
 * Encapsulates the WebSocket streamer and the Kafka producer/consumer
 */
class KafkaStreamListener<T : KafkaModel<T>>(
    kafkaCredentials: KafkaCredentials,
    endpoint: String,
    path: String,
    port: Int? = null,
    subscription: String,
    topic: String,
    serializer: KSerializer<*>,
    mapper: (String) -> T,
) {
    private val producerService = KafkaProducerService<T>(
        endpoint = endpoint,
        path = path,
        port = port,
        subscription = subscription,
        topic = topic,
        serializer = serializer,
        kafka = kafkaCredentials
    )
    private val consumerService = KafkaConsumerService(
        kafka = kafkaCredentials,
        topic = topic,
        mapper = mapper
    )

    /**
     * Subscribes to the WebSocket and funnels it through kafka
     */
    suspend fun subscribe() = flow {
        CoroutineScope(Dispatchers.Default).launch {
            producerService.subscribeToWebSocket()
        }
        consumerService.subscribe().collect {
            emit(it)
        }
    }
}