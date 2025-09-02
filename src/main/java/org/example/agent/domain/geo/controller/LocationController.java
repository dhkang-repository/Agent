package org.example.agent.domain.geo.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoRawDto;
import org.example.agent.domain.geo.service.GeoService;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.security.SecurityAuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/agent/v1.0/geo")
@RequiredArgsConstructor
public class LocationController {

    private final GeoService geoService;

    public record GoeFindRequest(
            String email,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate startDate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate endDate){
    }
    @GetMapping("/location")
    public ResponseEntity<?> getLocation(@Valid @ModelAttribute GoeFindRequest r,
                                         @AuthenticationPrincipal SecurityAuthUser securityUser) {
        List<GeoRawDto> list = geoService.search(r.email(), r.startDate(), r.endDate());

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        list
                )
        );
    }

    @PostMapping("/location")
    public ResponseEntity<?> location(@RequestBody GeoRawDto r,
                                      @AuthenticationPrincipal SecurityAuthUser securityUser) {
        geoService.save(r, securityUser.getUserId());

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        r
                )
        );
    }

}

