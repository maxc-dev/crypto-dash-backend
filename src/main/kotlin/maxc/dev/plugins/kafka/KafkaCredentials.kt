package maxc.dev.plugins.kafka

data class KafkaCredentials(
    val server: String = "${System.getenv("SERVER_IP")}:9092",
    val refreshMillis: Long = 200,
    val topicManager: KafkaTopicManager
)