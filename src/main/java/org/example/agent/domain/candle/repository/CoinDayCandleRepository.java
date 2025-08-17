package org.example.agent.domain.candle.repository;

import org.example.agent.entity.candle.CoinDayCandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CoinDayCandleRepository extends JpaRepository<CoinDayCandleEntity, Long> {
    Optional<CoinDayCandleEntity> findByMarketAndCandleDate(String market, LocalDate candleDate);
}
