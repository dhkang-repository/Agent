package org.example.agent.domain.candle.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.service.CandleSyncService;
import org.example.agent.global.constrant.GlobalConst;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(GlobalConst.BASE_URL)
public class CandleAdminController {

    private final CandleSyncService service;

    public record SyncResp(
            String market,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            int saved){
    }

    // 2-1) 쿼리 파라미터 방식
    @PostMapping(value = "/candle/day/sync", params = {"market","startDate","endDate"})
    public ResponseEntity syncByQuery(
            @RequestParam @NotBlank String market,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate endDate
    ) {
        int saved = service.syncRangeDailyCandles(market, startDate, endDate);
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        new SyncResp(market, startDate, endDate, saved)
                )
        );
    }

    // 2-2) JSON 바디 방식
    public record SyncReq(
            @NotBlank String market,
            @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {}
    @PostMapping(value = "/candle/day/sync", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity syncByJson(@RequestBody SyncReq req) {
        int saved = service.syncRangeDailyCandles(req.market(), req.startDate(), req.endDate());
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        new SyncResp(req.market(), req.startDate(), req.endDate(), saved)
                )
        );
    }
}

