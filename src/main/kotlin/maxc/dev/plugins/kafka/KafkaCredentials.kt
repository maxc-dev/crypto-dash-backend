package maxc.dev.plugins.kafka

data class KafkaCredentials(
    val server: String = "192.168.85.1:9092",
    val refreshMillis: Long = 200,
    val topicManager: KafkaTopicManager
)