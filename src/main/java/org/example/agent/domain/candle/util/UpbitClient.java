package org.example.agent.domain.candle.util;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.dto.UpbitDayCandleDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class UpbitClient {

    private final RestClient rest = RestClient.builder()
            .baseUrl("https://api.upbit.com")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    private static final DateTimeFormatter TO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public UpbitDayCandleDto[] getDayCandles(String market, int count, ZonedDateTime toKstExclusive) {
        String to = toKstExclusive.format(TO_FMT); // e.g. 2025-08-17T00:00:00+09:00 (exclusive)
        String url = String.format("/v1/candles/days?market=%s&count=%d&to=%s", market, count, to);
        return rest.get().uri(url).retrieve().body(UpbitDayCandleDto[].class);
    }

}
