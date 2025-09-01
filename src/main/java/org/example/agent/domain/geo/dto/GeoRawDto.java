package org.example.agent.domain.geo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.agent.entity.loc.GeoRawEntity;
import org.example.agent.entity.loc.GeoRawId;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link GeoRawEntity}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoRawDto implements Serializable {

    private Long userId;
    private LocalDateTime eventDt = LocalDateTime.now();
    private BigDecimal lat;
    private BigDecimal lon;
    private Float acc;
    private Float heading;
    private Float speed;
    private Point location;

    public GeoRawEntity toEntity() {
        GeoRawEntity e = new GeoRawEntity();
        e.setId(new GeoRawId(userId, eventDt));
        e.setLat(lat);
        e.setLon(lon);
        e.setAcc(acc);
        e.setHeading(heading);
        e.setSpeed(speed);
        e.setLocation(location);
        return e;
    }


}