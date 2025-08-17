package org.example.agent.domain.candle.controller;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.service.StrategyService;
import org.example.agent.domain.candle.strategy.TradingStrategy;
import org.example.agent.global.constrant.GlobalConst;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(GlobalConst.BASE_URL + "/strategy")
public class TradingStrategyController {

    private final StrategyService service;

    // 오늘 시그널(또는 마지막 일자 시그널)
    @GetMapping("/signal")
    public ResponseEntity<ResponseResult<TradingStrategy.StrategySignal>> signal(
            @RequestParam String market,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        service.latestSignal(market, startDate, endDate)
                )
        );
    }

    public record BacktestReq(String market,
                              LocalDate startDate,
                              LocalDate endDate,
                              Double feeRate) {}

    @PostMapping("/backtest")
    public ResponseEntity<ResponseResult<TradingStrategy.BacktestResult>> backtest(@RequestBody BacktestReq req) {
        double fee = req.feeRate() == null ? 0.0005 : req.feeRate(); // 기본 0.05% / 체결
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        service.backtest(req.market(), req.startDate(), req.endDate(), fee)
                )
        );
    }
}
