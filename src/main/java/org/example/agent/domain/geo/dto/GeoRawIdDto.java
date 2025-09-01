package org.example.agent.domain.geo.dto;

import lombok.Value;
import org.example.agent.entity.loc.GeoRawId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link GeoRawId}
 */
@Value
public class GeoRawIdDto implements Serializable {
    Long userId;
    LocalDateTime eventDt;
}