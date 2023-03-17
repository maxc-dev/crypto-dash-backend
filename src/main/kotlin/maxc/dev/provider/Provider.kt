package maxc.dev.provider

class Provider(val name: String, val endpoint: String, val path: String, private val subscription: String) {
    /**
     * Formats the subscription string with the symbol
     */
    fun getSubscriptionForSymbol(symbol: String) = subscription.format(symbol)
}