package maxc.dev.engine

/**
 * Periodically updates the price of an asset and updates the listener in the WebSocket API
 */
class PriceChangeManager {
    private val priceListeners = mutableListOf<PriceChangeListener>()

    fun addPriceChangeListener(listener: PriceChangeListener) {
        priceListeners.add(listener)
    }

    /**
     * Starts the price change manager by periodically retrieving the price of an asset
     * and sending it to the listeners
     */
    suspend fun start() {
        /*
        todo:
            - get the list of assets from the database
            - for each asset:
                    - get the current price
                    - get the price change
                    - format into a data class
            - send to the listeners
         */
    }
}