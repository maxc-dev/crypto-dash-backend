package maxc.dev.provider.price

import maxc.dev.plugins.kafka.KafkaModel

open class PriceModel(val price: Double, val timestamp: Long) : KafkaModel<PriceModel> {
    /**
     * Encodes the price model into a string
     * In the format of "price|timestamp"
     */
    override fun encode() = price.toString() + separator + timestamp.toString()

    override fun toString() = "$$price @ $timestamp"

    companion object {
        private const val separator = "|"

        /**
         * Mapper for decoding the encoded string
         */
        val mapper: (String) -> PriceModel =
            { PriceModel(it.substringBefore(separator).toDouble(), it.substringAfter(separator).toLong()) }
    }
}