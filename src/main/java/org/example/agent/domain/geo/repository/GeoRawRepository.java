package org.example.agent.domain.geo.repository;

import org.example.agent.entity.loc.GeoRawEntity;
import org.example.agent.entity.loc.GeoRawId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeoRawRepository extends JpaRepository<GeoRawEntity, GeoRawId> {
}