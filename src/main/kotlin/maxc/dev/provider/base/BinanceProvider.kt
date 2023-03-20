package maxc.dev.provider.base

import maxc.dev.provider.Provider

class BinanceProvider : Provider(
    name = "Binance",
    endpoint = "stream.binance.com",
    path = "ws/!miniTicker@arr",
    port = 443,
    subscription = ""
)