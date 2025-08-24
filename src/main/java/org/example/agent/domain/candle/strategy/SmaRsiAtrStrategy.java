package org.example.agent.domain.candle.strategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.example.agent.domain.candle.util.IndicatorUtil.*;

public class SmaRsiAtrStrategy implements TradingStrategy {
    private final int shortSma;
    private final int longSma;
    private final int rsiPeriod;
    private final int atrPeriod;
    private final double atrStopMult;
    private final double takeProfitR; // R multiple

    public SmaRsiAtrStrategy() {
        this(20, 50, 14, 14, 2.0, 2.0);
    }

    public SmaRsiAtrStrategy(int shortSma,
                             int longSma,
                             int rsiPeriod,
                             int atrPeriod,
                             double atrStopMult,
                             double takeProfitR) {
        this.shortSma = shortSma;
        this.longSma = longSma;
        this.rsiPeriod = rsiPeriod;
        this.atrPeriod = atrPeriod;
        this.atrStopMult = atrStopMult;
        this.takeProfitR = takeProfitR;
    }

    @Override
    public StrategySignal getSignal(List<Candle> candles) {
        if (candles == null || candles.size() < Math.max(longSma, Math.max(rsiPeriod, atrPeriod)) + 2) {
            return new StrategySignal(Action.HOLD, "not-enough-data");
        }
        int n = candles.size();
        double[] close = candles.stream().mapToDouble(Candle::close).toArray();
        double[] high  = candles.stream().mapToDouble(Candle::high).toArray();
        double[] low   = candles.stream().mapToDouble(Candle::low).toArray();

        double[] smaS = sma(close, shortSma);
        double[] smaL = sma(close, longSma);
        double[] rsiA = rsi(close, rsiPeriod);

        int i = n - 1;
        int j = n - 2;

        boolean goldenCross = !Double.isNaN(smaS[j]) && !Double.isNaN(smaL[j])
                && smaS[j] <= smaL[j] && smaS[i] > smaL[i];

        boolean deadCross   = !Double.isNaN(smaS[j]) && !Double.isNaN(smaL[j])
                && smaS[j] >= smaL[j] && smaS[i] < smaL[i];

        if (goldenCross && rsiA[i] < 60)
            return new StrategySignal(Action.BUY, "golden-cross & rsi<60");

        if (deadCross)
            return new StrategySignal(Action.SELL, "dead-cross");

        return new StrategySignal(Action.HOLD, "no-edge");
    }

    @Override
    public BacktestResult backtest(List<Candle> candles, double feeRate) {
        if (candles == null || candles.size() < Math.max(longSma, Math.max(rsiPeriod, atrPeriod)) + 2) {
            return new BacktestResult(List.of(), 0, 0, 0);
        }

        int n = candles.size();

        double[] close = candles.stream().mapToDouble(Candle::close).toArray();
        double[] high  = candles.stream().mapToDouble(Candle::high).toArray();
        double[] low   = candles.stream().mapToDouble(Candle::low).toArray();

        double[] smaS = sma(close, shortSma);
        double[] smaL = sma(close, longSma);
        double[] rsiA = rsi(close, rsiPeriod);
        double[] atrA = atr(high, low, close, atrPeriod);

        boolean inPos = false;

        double entry = 0, stop = 0, takeProfit = 0, peak = 0;

        LocalDate entryDate = null;

        List<Trade> trades = new ArrayList<>();

        double equity = 0;      // 누적 PnL
        double peakEq = 0;      // 드로다운 계산
        double maxDD  = 0;

        for (int i = 1; i < n; i++) {
            if (!inPos) {
                // 진입 조건
                boolean goldenCross = !Double.isNaN(smaS[i-1]) && !Double.isNaN(smaL[i-1])
                        && smaS[i-1] <= smaL[i-1] && smaS[i] > smaL[i];
                if (goldenCross && rsiA[i] < 60 && !Double.isNaN(atrA[i])) {
                    inPos = true;
                    entry = close[i]; // 종가 체결 가정
                    double riskR = atrStopMult * atrA[i];
                    stop = entry - riskR;
                    takeProfit = entry + takeProfitR * riskR;
                    peak = entry;
                    entryDate = candles.get(i).date();

                    // 매수/매도 수수료(왕복) 고려 위해, 진입 시 수수료만큼 equity를 감소시키는 접근도 가능
                    // 여기선 체결 시/청산 시 각각 fee 적용
                    equity -= entry * feeRate;
                }
            } else {
                // 트레일링 스탑(선택): ATR 기반으로 스탑 상향
                if (!Double.isNaN(atrA[i])) {
                    double trail = close[i] - atrStopMult * atrA[i];
                    if (trail > stop) stop = trail;
                }
                if (close[i] > peak) peak = close[i];

                boolean stopHit = close[i] <= stop;
                boolean takeHit = close[i] >= takeProfit;
                boolean deadCross = !Double.isNaN(smaS[i-1]) && !Double.isNaN(smaL[i-1])
                        && smaS[i-1] >= smaL[i-1] && smaS[i] < smaL[i];

                if (stopHit || takeHit || deadCross) {
                    double exit = close[i];
                    double pnl  = (exit - entry) - (entry * feeRate) - (exit * feeRate);
                    double rMul = (exit - entry) / (entry - stop);

                    equity += pnl;
                    trades.add(new Trade(entryDate, entry, candles.get(i).date(), exit, pnl, rMul));

                    peakEq = Math.max(peakEq, equity);
                    maxDD = Math.max(maxDD, peakEq - equity);

                    inPos = false;
                }
            }
        }

        long wins = trades.stream().filter(t -> t.pnl() > 0).count();
        double winRate = trades.isEmpty() ? 0 : (wins * 100.0 / trades.size());
        double maxDrawdown = maxDD;

        return new BacktestResult(trades, equity, winRate, maxDrawdown);
    }
}
