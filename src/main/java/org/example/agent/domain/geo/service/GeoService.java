package org.example.agent.domain.geo.service;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.domain.geo.dto.GeoRawDto;
import org.example.agent.domain.geo.repository.GeoRawRepository;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.DefineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoService {
    private final AuthUserRepository authUserRepository;
    private final GeoRawRepository geoRawRepository;

    @Transactional
    public void save(GeoRawDto geoRawDto, Long userId) {
        geoRawDto.setUserId(userId);
        if(geoRawDto.getLat() == null || geoRawDto.getLon() == null) {
            return;
        }

        geoRawRepository.save(geoRawDto.toEntity());
    }

    public List<GeoRawDto> search(String email, LocalDate from, LocalDate to) {
        AuthUserEntity userEntity = authUserRepository.findByEmail(email).orElseThrow(
                () -> new DefineException(ErrorCode.ENTITY_NOT_EXIST, "user not found")
        );

        return geoRawRepository.search(userEntity.getId(), from, to).stream().map(GeoRawDto::from).toList();
    }

}
