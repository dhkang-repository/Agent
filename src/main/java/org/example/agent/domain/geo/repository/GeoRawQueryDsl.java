package org.example.agent.domain.geo.repository;

import org.example.agent.entity.loc.GeoRawEntity;

import java.time.LocalDate;
import java.util.List;

public interface GeoRawQueryDsl {
    List<GeoRawEntity> search(Long userId, LocalDate from, LocalDate to);
}
