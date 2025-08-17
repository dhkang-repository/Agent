package org.example.agent.domain.candle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record UpbitDayCandleDto(
        String market,
        @JsonProperty("candle_date_time_kst") String candleDateTimeKst,   // "2025-08-16T00:00:00"
        @JsonProperty("candle_date_time_utc") String candleDateTimeUtc,
        @JsonProperty("opening_price") BigDecimal openingPrice,
        @JsonProperty("high_price") BigDecimal highPrice,
        @JsonProperty("low_price") BigDecimal lowPrice,
        @JsonProperty("trade_price") BigDecimal tradePrice,
        Long timestamp,
        @JsonProperty("candle_acc_trade_price") BigDecimal candleAccTradePrice,
        @JsonProperty("candle_acc_trade_volume") BigDecimal candleAccTradeVolume,
        @JsonProperty("prev_closing_price") BigDecimal prevClosingPrice,
        @JsonProperty("change_price") BigDecimal changePrice,
        @JsonProperty("change_rate") BigDecimal changeRate
) {}

