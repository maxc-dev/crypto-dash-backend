package maxc.dev.plugins.kafka

data class KafkaCredentials(
    val server: String = "192.168.0.73:9092",
    val refreshMillis: Long = 200,
    val topicManager: KafkaTopicManager
)