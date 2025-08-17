package org.example.agent.domain.candle.strategy;

import java.time.LocalDate;
import java.util.List;

public interface TradingStrategy {
    StrategySignal getSignal(List<Candle> candles); // 마지막 일자 기준 BUY/SELL/HOLD
    BacktestResult backtest(List<Candle> candles, double feeRate); // 단위: 0.001 = 0.1%

    record Candle(LocalDate date, double open, double high, double low, double close) {}
    enum Action { BUY, SELL, HOLD }
    record StrategySignal(Action action, String reason) {}
    record Trade(LocalDate entryDate, double entry, LocalDate exitDate, double exit, double pnl, double rMultiple) {}
    record BacktestResult(List<Trade> trades, double totalPnl, double winRate, double maxDrawdown) {}
}
