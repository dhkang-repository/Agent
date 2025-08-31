package org.example.agent.domain.geo.controller;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoReportDto;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/agent/v1.0/geo")
@RequiredArgsConstructor
public class LocationController {

    @PostMapping("/location")
    public ResponseEntity<?> location(@RequestBody GeoReportDto r,
                                      @RequestHeader(value="Authorization", required=false) String auth) {
        // 1) 인증/JWT/HMAC 검사 (생략 X)
        // 2) 필터링/정규화(좌표 범위, ts 역행 제거 등)
        System.out.println(r);
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        r
                )
        );
    }

}

