package maxc.dev.provider

open class Provider(val name: String, val endpoint: String, val path: String, private val subscription: String, val port: Int? = null) {
    /**
     * Formats the subscription string with the symbol
     */
    fun getSubscriptionForSymbol(symbol: String) = subscription.format(symbol)
}