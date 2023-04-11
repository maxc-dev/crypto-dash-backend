package maxc.dev.engine

interface PriceChangeListener {
    suspend fun onPricesChange(data: List<PriceChange>)
}