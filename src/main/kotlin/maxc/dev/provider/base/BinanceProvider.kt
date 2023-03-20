package maxc.dev.provider.base

import maxc.dev.provider.Provider

class BinanceProvider : Provider(
    name = "Binance",
    endpoint = "ws-api.binance.com",
    path = "ws-api/v3",
    port = 443,
    subscription = subscription
) {
    companion object {
        const val subscription = """
            {
              "id": "ddbfb65f-9ebf-42ec-8240-8f0f91de0867",
              "method": "avgPrice",
              "params": {
                "symbol": "%s"
              }
            }
        """
    }
}