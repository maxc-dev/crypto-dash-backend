package maxc.dev.plugins.kafka
val localServer: String by project

data class KafkaCredentials(
    val server: String = "$localServer:9092",
    val refreshMillis: Long = 200,
    val topicManager: KafkaTopicManager
)