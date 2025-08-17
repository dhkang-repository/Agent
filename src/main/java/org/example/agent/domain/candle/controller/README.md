## /signal Request
    GET /strategy/signal?market=KRW-BTC&startDate=2024-01-01&endDate=2025-08-17
## /signal Response
    { "action": "BUY", "reason": "golden-cross & rsi<60" }
## /backtest Request
    POST /strategy/backtest
    Body: { "market":"KRW-BTC", "startDate":"2024-01-01", "endDate":"2025-08-17", "feeRate":0.0005 }
## /backtest Response
    {
      "trades": [
        { "entryDate":"2024-02-15", "entry":71500000, "exitDate":"2024-03-01", "exit":76000000, "pnl":4490000.0, "rMultiple":2.1 }
      ],
      "totalPnl": 12560000.0,
      "winRate": 57.1,
      "maxDrawdown": 3800000.0
    }
