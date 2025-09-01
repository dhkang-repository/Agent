package org.example.agent.domain.geo.service;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoRawDto;
import org.example.agent.domain.geo.repository.GeoRawRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeoService {
    private final GeoRawRepository geoRawRepository;

    @Transactional
    public void save(GeoRawDto geoRawDto, Long userId) {
        geoRawDto.setUserId(userId);
        geoRawRepository.save(geoRawDto.toEntity());
    }

}
