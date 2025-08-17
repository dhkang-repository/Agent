package org.example.agent.global.schedule;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.service.CandleSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CandleScheduler {

    private final CandleSyncService service;

    // 매일 00:10 KST에 “어제 일봉” 갱신
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void syncDaily() {
        // 필요한 마켓 목록 자유롭게
        String[] markets = {"KRW-BTC", "KRW-ETH"};
        for (String m : markets) {
            service.syncLatestDay(m);
        }
    }
}
