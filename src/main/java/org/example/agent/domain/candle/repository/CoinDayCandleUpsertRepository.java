package org.example.agent.domain.candle.repository;

import org.springframework.data.jpa.repository.Modifying;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CoinDayCandleUpsertRepository {
    @Modifying
    @org.springframework.data.jpa.repository.Query(value = """
      INSERT INTO coin_day_candle
      (market, candle_date, open_price, high_price, low_price, close_price,
       acc_trade_price, acc_trade_volume, prev_close_price, change_price, change_rate, source_ts)
      VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12)
      ON DUPLICATE KEY UPDATE
        open_price=VALUES(open_price),
        high_price=VALUES(high_price),
        low_price=VALUES(low_price),
        close_price=VALUES(close_price),
        acc_trade_price=VALUES(acc_trade_price),
        acc_trade_volume=VALUES(acc_trade_volume),
        prev_close_price=VALUES(prev_close_price),
        change_price=VALUES(change_price),
        change_rate=VALUES(change_rate),
        source_ts=VALUES(source_ts)
      """, nativeQuery = true)
    void upsert(String market, LocalDate date,
                BigDecimal o, BigDecimal h, BigDecimal l, BigDecimal c,
                BigDecimal atp, BigDecimal atv,
                BigDecimal pclose, BigDecimal chg, BigDecimal rate, Long ts);
}

