package org.example.agent.domain.candle.service;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.util.UpbitClient;
import org.example.agent.domain.candle.dto.UpbitDayCandleDto;
import org.example.agent.domain.candle.repository.CoinDayCandleRepository;
import org.example.agent.entity.candle.CoinDayCandleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandleSyncService {

    private final UpbitClient upbit;
    private final CoinDayCandleRepository repo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 지정 마켓에 대해 최근 1년치 일봉을 DB에 upsert */
    @Transactional
    public int syncOneYearDailyCandles(String market) {

        LocalDate endDate = LocalDate.now(KST);

        LocalDate startDate = endDate.minusDays(365);

        ZonedDateTime cursorExclusive = endDate.atStartOfDay(KST); // 'to' exclusive
        int totalSaved = 0;

        while (true) {
            UpbitDayCandleDto[] arr = upbit.getDayCandles(market, 200, cursorExclusive);
            if (arr == null || arr.length == 0) break;

            // 내림차순 → 오름차순으로 정렬해서 저장 안정성 확보
            List<UpbitDayCandleDto> list = Arrays.stream(arr)
                    .sorted(Comparator.comparing(UpbitDayCandleDto::candleDateTimeKst))
                    .collect(Collectors.toList());

            for (UpbitDayCandleDto d : list) {
                LocalDate kstDate = LocalDate.parse(d.candleDateTimeKst().substring(0, 10)); // yyyy-MM-dd
                if (kstDate.isBefore(startDate)) continue;       // 1년 밖은 스킵
                if (kstDate.isAfter(endDate)) continue;          // 미래 스킵(안 나오지만 안전장치)

                // upsert
                CoinDayCandleEntity c = repo.findByMarketAndCandleDate(market, kstDate)
                        .orElseGet(() -> CoinDayCandleEntity.builder().market(market).candleDate(kstDate).build());

                c.setOpenPrice(nz(d.openingPrice()));
                c.setHighPrice(nz(d.highPrice()));
                c.setLowPrice(nz(d.lowPrice()));
                c.setClosePrice(nz(d.tradePrice()));
                c.setAccTradePrice(nz(d.candleAccTradePrice()));
                c.setAccTradeVolume(nz(d.candleAccTradeVolume()));
                c.setPrevClosePrice(d.prevClosingPrice());
                c.setChangePrice(d.changePrice());
                c.setChangeRate(d.changeRate());
                c.setSourceTs(d.timestamp());

                repo.save(c);
                totalSaved++;
            }

            // 다음 페이지 커서: 가장 오래된 캔들의 KST 자정 시각보다 1초 이전
            UpbitDayCandleDto oldest = list.get(0);
            LocalDate oldestDate = LocalDate.parse(oldest.candleDateTimeKst().substring(0,10));
            if (oldestDate.isBefore(startDate) || oldestDate.equals(startDate)) break;

            cursorExclusive = oldestDate.atStartOfDay(KST).minusSeconds(1);
        }
        return totalSaved;
    }

    /** 최근 하루치만 갱신(스케줄러에서 호출) */
    @Transactional
    public int syncLatestDay(String market) {
        ZonedDateTime tomorrow0 = LocalDate.now(KST).plusDays(1).atStartOfDay(KST);

        UpbitDayCandleDto[] arr = upbit.getDayCandles(market, 5, tomorrow0); // 최근 5개 받으면 어제/그제 보정도 가능

        int saved = 0;
        if (arr != null) {
            for (UpbitDayCandleDto d : arr) {
                LocalDate kstDate = LocalDate.parse(d.candleDateTimeKst().substring(0,10));
                if (kstDate.isAfter(LocalDate.now(KST))) continue;
                CoinDayCandleEntity c = repo.findByMarketAndCandleDate(market, kstDate)
                        .orElseGet(() -> CoinDayCandleEntity.builder().market(market).candleDate(kstDate).build());
                c.setOpenPrice(nz(d.openingPrice()));
                c.setHighPrice(nz(d.highPrice()));
                c.setLowPrice(nz(d.lowPrice()));
                c.setClosePrice(nz(d.tradePrice()));
                c.setAccTradePrice(nz(d.candleAccTradePrice()));
                c.setAccTradeVolume(nz(d.candleAccTradeVolume()));
                c.setPrevClosePrice(d.prevClosingPrice());
                c.setChangePrice(d.changePrice());
                c.setChangeRate(d.changeRate());
                c.setSourceTs(d.timestamp());
                repo.save(c);
                saved++;
            }
        }
        return saved;
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }


    @Transactional
    public int syncRangeDailyCandles(String market,
                                     LocalDate startDate,
                                     LocalDate endDate) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate/endDate required");
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("endDate must be >= startDate");

        final ZoneId KST = ZoneId.of("Asia/Seoul");
        // endDate를 포함하려면 to(exclusive)를 'endDate + 1일 00:00'으로
        ZonedDateTime cursorExclusive = endDate.plusDays(1).atStartOfDay(KST);

        int totalSaved = 0;

        while (true) {
            var arr = upbit.getDayCandles(market, 200, cursorExclusive);
            if (arr == null || arr.length == 0) break;

            // 최신→과거로 오니, 저장은 오름차순으로
            var list = Arrays.stream(arr)
                    .sorted(Comparator.comparing(UpbitDayCandleDto::candleDateTimeKst))
                    .toList();

            for (var d : list) {
                LocalDate kstDate = LocalDate.parse(d.candleDateTimeKst().substring(0, 10)); // yyyy-MM-dd
                if (kstDate.isBefore(startDate) || kstDate.isAfter(endDate)) continue;

                CoinDayCandleEntity c = repo.findByMarketAndCandleDate(market, kstDate)
                        .orElseGet(() -> CoinDayCandleEntity.builder().market(market).candleDate(kstDate).build());

                c.setOpenPrice(nz(d.openingPrice()));
                c.setHighPrice(nz(d.highPrice()));
                c.setLowPrice(nz(d.lowPrice()));
                c.setClosePrice(nz(d.tradePrice()));
                c.setAccTradePrice(nz(d.candleAccTradePrice()));
                c.setAccTradeVolume(nz(d.candleAccTradeVolume()));
                c.setPrevClosePrice(d.prevClosingPrice());
                c.setChangePrice(d.changePrice());
                c.setChangeRate(d.changeRate());
                c.setSourceTs(d.timestamp());

                repo.save(c);
                totalSaved++;
            }

            // 다음 페이지 커서(과거로 이동)
            LocalDate oldest = LocalDate.parse(list.get(0).candleDateTimeKst().substring(0, 10));
            // 이미 startDate까지 내려왔으면 종료
            if (!oldest.isAfter(startDate)) break;
            cursorExclusive = oldest.atStartOfDay(KST).minusSeconds(1);
        }
        return totalSaved;
    }

}
