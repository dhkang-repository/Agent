package org.example.agent.domain.geo.controller;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoReportDto;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent/v1.0/geo")
@RequiredArgsConstructor
public class GeoController {
    private final KafkaTemplate<String, GeoReportDto> kafka;
    private final String topic = "geo.locations.v1";

    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestBody GeoReportDto r,
                                       @RequestHeader(value="Authorization", required=false) String auth) {
        // 1) 인증/JWT/HMAC 검사 (생략 X)
        // 2) 필터링/정규화(좌표 범위, ts 역행 제거 등)
        kafka.send(topic, r.userId(), r); // key=userId → per-user ordering
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        r
                )
        );
    }
}

