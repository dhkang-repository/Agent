package org.example.agent.domain.candle.controller;


import lombok.RequiredArgsConstructor;
import org.example.agent.domain.candle.service.StrategyService;
import org.example.agent.domain.candle.strategy.TradingStrategy;
import org.example.agent.global.constrant.GlobalConst;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(GlobalConst.BASE_URL + "/strategy")
public class StrategyController {

    private final StrategyService service;

    // 오늘 시그널(또는 마지막 일자 시그널)
    @GetMapping("/signal")
    public TradingStrategy.StrategySignal signal(
            @RequestParam String market,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return service.latestSignal(market, startDate, endDate);
    }

    public record BacktestReq(String market,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              Double feeRate) {}

    @PostMapping("/backtest")
    public TradingStrategy.BacktestResult backtest(@RequestBody BacktestReq req) {
        double fee = req.feeRate() == null ? 0.0005 : req.feeRate(); // 기본 0.05% / 체결
        return service.backtest(req.market(), req.startDate(), req.endDate(), fee);
    }
}
