package org.example.agent.entity.loc;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;

@Getter @Setter
@Entity
@Table(
        name = "tbl_geo_raw",
        schema = "agent_db", catalog = "agent_db",
        indexes = {
                // 이메일 조회 용도(선택). 공간 인덱스는 DB DDL에서 이미 생성됨.
                @Index(name = "idx_email", columnList = "email")
        }
)

public class GeoRawEntity {

    @EmbeddedId
    private GeoRawId id;

    @Column(name = "lat", nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lon", nullable = false, precision = 10, scale = 6)
    private BigDecimal lon;

    @Column(name = "acc")
    private Float acc;

    @Column(name = "heading")
    private Float heading;

    @Column(name = "speed")
    private Float speed;

    // MySQL 8 공간 타입. SRID 4326 명시
    @Column(name = "location", nullable = false, columnDefinition = "POINT SRID 4326 NOT NULL")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Point location;

    /** lat/lon → location(POINT, SRID=4326) 자동 세팅 */
    @PrePersist
    @PreUpdate
    private void syncPoint() {
        if (lat == null || lon == null) return;
        double xLon = lon.doubleValue();
        double yLat = lat.doubleValue();
        if (location == null ||
                location.getX() != xLon || location.getY() != yLat || location.getSRID() != 4326) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = gf.createPoint(new Coordinate(xLon, yLat));
            this.location.setSRID(4326);
        }
    }
}
