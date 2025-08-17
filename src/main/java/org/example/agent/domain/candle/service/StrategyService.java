package org.example.agent.domain.candle.service;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.repository.CoinDayCandleRepository;
import org.example.agent.domain.candle.strategy.SmaRsiAtrStrategy;
import org.example.agent.domain.candle.strategy.TradingStrategy;
import org.example.agent.entity.candle.CoinDayCandleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final CoinDayCandleRepository repo;

    private List<TradingStrategy.Candle> mapCandles(List<CoinDayCandleEntity> rows) {
        return rows.stream().map(r -> new TradingStrategy.Candle(
                r.getCandleDate(),
                nz(r.getOpenPrice()),
                nz(r.getHighPrice()),
                nz(r.getLowPrice()),
                nz(r.getClosePrice())
        )).toList();
    }

    private static double nz(java.math.BigDecimal v) { return v == null ? 0d : v.doubleValue(); }

    @Transactional(readOnly = true)
    public TradingStrategy.StrategySignal latestSignal(String market, LocalDate start, LocalDate end) {
        List<CoinDayCandleEntity> rows = repo.findByMarketAndCandleDateBetweenOrderByCandleDateAsc(market, start, end);
        var candles = mapCandles(rows);
        TradingStrategy strategy = new SmaRsiAtrStrategy();
        return strategy.getSignal(candles);
    }

    @Transactional(readOnly = true)
    public TradingStrategy.BacktestResult backtest(String market, LocalDate start, LocalDate end, double feeRate) {
        // 초반 인디케이터 워밍업을 위해 start 이전 며칠(예: 60일) 더 가져와도 좋음
        List<CoinDayCandleEntity> rows = repo.findByMarketAndCandleDateBetweenOrderByCandleDateAsc(market, start.minusDays(60), end);
        var candles = mapCandles(rows);
        TradingStrategy strategy = new SmaRsiAtrStrategy();
        return strategy.backtest(candles, feeRate);
    }
}
