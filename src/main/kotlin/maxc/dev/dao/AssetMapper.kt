package maxc.dev.dao

import maxc.dev.engine.PriceChange

object AssetMapper {
    fun List<Asset>.toPriceChangeModel(): PriceChange = with(priceToPercent(this)) {
        PriceChange(
            name = this@toPriceChangeModel.first().name,
            price = this@toPriceChangeModel.first().price,
            change1s = this.getOrNull(1),
            change1m = this.getOrNull(2),
            change30m = this.getOrNull(3),
            change1h = this.getOrNull(4),
            change12h = this.getOrNull(5),
            change1d = this.getOrNull(6),
        )
    }

    /**
     * Converts two prices to a percentage change
     */
    private fun priceToPercent(asset: List<Asset>) = with(asset.first().price) {
        asset.map { next -> (this - next.price) / this }
    }
}