package org.example.agent.entity.candle;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbL_coin_day_candle", catalog = "agent_db", schema = "agent_db",
        uniqueConstraints = @UniqueConstraint(name="uk_market_date", columnNames = {"market","candle_date"}))
public class CoinDayCandleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=20) private String market;
    @Column(name="candle_date", nullable=false) private LocalDate candleDate;

    @Column(name="open_price",  nullable=false, precision=24, scale=8) private BigDecimal openPrice;
    @Column(name="high_price",  nullable=false, precision=24, scale=8) private BigDecimal highPrice;
    @Column(name="low_price",   nullable=false, precision=24, scale=8) private BigDecimal lowPrice;
    @Column(name="close_price", nullable=false, precision=24, scale=8) private BigDecimal closePrice;

    @Column(name="acc_trade_price",  nullable=false, precision=30, scale=8) private BigDecimal accTradePrice;
    @Column(name="acc_trade_volume", nullable=false, precision=30, scale=8) private BigDecimal accTradeVolume;

    @Column(name="prev_close_price", precision=24, scale=8) private BigDecimal prevClosePrice;
    @Column(name="change_price",     precision=24, scale=8) private BigDecimal changePrice;
    @Column(name="change_rate",      precision=16, scale=8) private BigDecimal changeRate;

    @Column(name="source_ts") private Long sourceTs;
}
