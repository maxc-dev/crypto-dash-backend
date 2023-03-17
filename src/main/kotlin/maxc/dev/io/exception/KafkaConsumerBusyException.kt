package maxc.dev.io.exception

import maxc.dev.plugins.kafka.KafkaConsumerService

class KafkaConsumerBusyException(consumer: KafkaConsumerService<*>) : RuntimeException("Kafka consumer is busy: $consumer")