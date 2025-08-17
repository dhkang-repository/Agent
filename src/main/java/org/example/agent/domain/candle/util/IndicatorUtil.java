package org.example.agent.domain.candle.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndicatorUtil {


    /** 단순이동평균(SMA) */
    public static double[] sma(double[] v, int period) {
        double[] out = new double[v.length];
        Arrays.fill(out, Double.NaN);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
            sum += v[i];
            if (i >= period) sum -= v[i - period];
            if (i >= period - 1) out[i] = sum / period;
        }
        return out;
    }

    /** RSI(14) 기본 구현 (Wilders smoothing) */
    public static double[] rsi(double[] close, int period) {
        double[] out = new double[close.length];
        Arrays.fill(out, Double.NaN);
        double gain = 0, loss = 0;

        for (int i = 1; i < close.length; i++) {
            double change = close[i] - close[i - 1];
            double g = Math.max(0, change);
            double l = Math.max(0, -change);
            if (i <= period) {
                gain += g; loss += l;
                if (i == period) {
                    double avgGain = gain / period;
                    double avgLoss = loss / period;
                    out[i] = avgLoss == 0 ? 100 : 100 - (100 / (1 + (avgGain / avgLoss)));
                }
            } else {
                // Wilder
                double prevAvgGain = (out[i - 1] == Double.NaN) ? gain / period : Double.NaN;
                gain = (gain - gain / period) + g;
                loss = (loss - loss / period) + l;

                double avgGain = ( (out[i - 1] == Double.NaN) ? (gain/period) : ((gain/period)) );
                double avgLoss = ( (out[i - 1] == Double.NaN) ? (loss/period) : ((loss/period)) );

                double rs = avgLoss == 0 ? Double.POSITIVE_INFINITY : (avgGain / avgLoss);
                out[i] = 100 - (100 / (1 + rs));
            }
        }
        return out;
    }

    /** ATR(14). prevClose는 없으면 이전 close 사용 */
    public static double[] atr(double[] high, double[] low, double[] close, int period) {
        double[] tr = new double[close.length];
        Arrays.fill(tr, Double.NaN);

        for (int i = 0; i < close.length; i++) {
            if (i == 0) {
                tr[i] = high[i] - low[i];
            } else {
                double prevClose = close[i - 1];
                double a = high[i] - low[i];
                double b = Math.abs(high[i] - prevClose);
                double c = Math.abs(low[i] - prevClose);
                tr[i] = Math.max(a, Math.max(b, c));
            }
        }

        double[] out = new double[close.length];
        Arrays.fill(out, Double.NaN);

        double sum = 0;
        for (int i = 0; i < tr.length; i++) {
            sum += tr[i];
            if (i == period - 1) {
                out[i] = sum / period;
            } else if (i >= period) {
                // Wilder smoothing
                out[i] = (out[i - 1] * (period - 1) + tr[i]) / period;
            }
        }
        return out;
    }

}
