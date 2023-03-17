package maxc.dev.plugins.kafka

import maxc.dev.provider.Provider
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import java.util.logging.Logger

class KafkaTopicManager(private val adminClient: AdminClient) {
    private val log: Logger = Logger.getLogger(this::class.java.name)

    private var localTopics: HashSet<String> = HashSet()

    init {
        localTopics = getTopics()
    }

    /**
     * forceReload will load the topics from the kafka server instead of a local copy
     */
    private fun isTopicRegistered(topic: String, forceReload: Boolean = false): Boolean {
        return if (forceReload) getTopics().contains(topic) else localTopics.contains(topic)
    }

    /**
     * Retrieves topics from kafka server and updates the local copy if there are more
     * topics discovered
     */
    private fun getTopics(): HashSet<String> {
        val fresh = adminClient.listTopics()?.names()?.get() ?: emptySet()
        localTopics.addAll(fresh)
        return HashSet(fresh)
    }

    private fun registerTopic(topic: String) {
        if (isTopicRegistered(topic)) {
            log.info("Topic $topic is already registered")
        }
        adminClient.createTopics(listOf(NewTopic(topic, 1, 1)))
    }

    fun createTopicCreateIfAbsent(topic: String) {
        if (!isTopicRegistered(topic)) {
            registerTopic(topic)
        }
    }

    companion object {
        /** Format is: WS_EXCHANGE_SYMBOL */
        private const val kafkaOrderBookFormat = "WS_%s_%s"

        fun getTopic(provider: Provider, symbol: String): String =
            kafkaOrderBookFormat.format(provider.name, symbol)

    }
}