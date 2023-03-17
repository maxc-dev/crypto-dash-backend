package maxc.dev.plugins.kafka

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import maxc.dev.io.exception.KafkaConsumerBusyException
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.logging.Logger
import kotlin.random.Random

class KafkaConsumerService<T : KafkaModel<T>>(
    private val kafka: KafkaCredentials,
    private val topic: String,
    private val mapper: (String) -> T
) {
    private val log: Logger = Logger.getLogger(KafkaConsumerService::class.java.name)

    @Volatile
    private var consuming = false

    private val consumer = KafkaConsumer<String, String>(
        mapOf(
            "bootstrap.servers" to kafka.server,
            "key.deserializer" to StringDeserializer::class.java,
            "value.deserializer" to StringDeserializer::class.java,
            "group.id" to "$topic-${Random(1000).nextInt(256000)}",
            "auto.offset.reset" to "latest"
        )
    )

    /**
     * Subscribes to the internal order book topic and returns a flow of order books
     */
    fun subscribe(): Flow<T>  {
        if (consuming) {
            log.warning("$this is already consuming")
            throw KafkaConsumerBusyException(this@KafkaConsumerService)
        }

        // subscribe to the topic
        consuming = true
        log.info("Subscribing kafka consumer to $topic")
        consumer.subscribe(listOf(topic))

        // return a new flow which consumes from kafka
        return flow {
            while (consuming) {
                consumer.poll(Duration.ofMillis(kafka.refreshMillis))
                    .forEach {
                        emit(KafkaModel.decode(it.value(), mapper))
                    }
            }

        }
    }

    /**
     * Closes the kafka consumer
     */
    fun unsubscribe() {
        consumer.unsubscribe()
        consuming = false
    }
}