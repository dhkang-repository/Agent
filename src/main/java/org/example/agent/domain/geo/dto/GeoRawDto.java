package org.example.agent.domain.geo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
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
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoRawDto implements Serializable {

    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDt = LocalDateTime.now();
    private BigDecimal lat;
    private BigDecimal lon;
    private Float acc;
    private Float heading;
    private Float speed;
    @JsonIgnore
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


    public static GeoRawDto from(GeoRawEntity geoRawEntity) {
        return GeoRawDto.builder()
                .userId(geoRawEntity.getId().getUserId())
                .eventDt(geoRawEntity.getId().getEventDt())
                .lat(geoRawEntity.getLat())
                .lon(geoRawEntity.getLon())
                .acc(geoRawEntity.getAcc())
                .heading(geoRawEntity.getHeading())
                .speed(geoRawEntity.getSpeed())
                .location(geoRawEntity.getLocation())
                .build();
    }
}