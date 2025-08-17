package org.example.agent.domain.candle.repository;

import org.example.agent.entity.candle.CoinDayCandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CoinDayCandleRepository extends JpaRepository<CoinDayCandleEntity, Long> {
    Optional<CoinDayCandleEntity> findByMarketAndCandleDate(String market, LocalDate candleDate);

    List<CoinDayCandleEntity> findByMarketAndCandleDateBetweenOrderByCandleDateAsc(
            String market, LocalDate startDate, LocalDate endDate
    );

    // 백테스트 편의를 위해 시작 이전의 하루를 더 가져올 때 사용 (이평/RSI 초기화용)
    List<CoinDayCandleEntity> findByMarketAndCandleDateLessThanEqualOrderByCandleDateDesc(
            String market, LocalDate candleDate
    );
}
