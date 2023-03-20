package maxc.dev.plugins.kafka

interface KafkaModel<T> {
    fun encode(): List<String>

    companion object {
        // decode with mapper
        fun <T> decode(encoded: String, mapper: (String) -> T): T = mapper(encoded)
    }
}