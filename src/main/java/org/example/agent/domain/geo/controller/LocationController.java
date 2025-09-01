package org.example.agent.domain.geo.controller;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoRawDto;
import org.example.agent.domain.geo.service.GeoService;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.security.SecurityAuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/agent/v1.0/geo")
@RequiredArgsConstructor
public class LocationController {

    private final GeoService geoService;

    @PostMapping("/location")
    public ResponseEntity<?> location(@RequestBody GeoRawDto r,
                                      @AuthenticationPrincipal SecurityAuthUser securityUser) {
        // 1) 인증/JWT/HMAC 검사 (생략 X)
        // 2) 필터링/정규화(좌표 범위, ts 역행 제거 등)
        geoService.save(r, securityUser.getUserId());

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        r
                )
        );
    }

}

