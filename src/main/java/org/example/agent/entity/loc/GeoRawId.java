package org.example.agent.entity.loc;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class GeoRawId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // DATETIME(3) 정밀도 유지
    @Column(name = "event_dt", nullable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime eventDt;

    public GeoRawId() {}

    public GeoRawId(Long userId, LocalDateTime eventDt) {
        this.userId = userId;
        this.eventDt = eventDt;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getEventDt() { return eventDt; }
    public void setEventDt(LocalDateTime eventDt) { this.eventDt = eventDt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoRawId)) return false;
        GeoRawId that = (GeoRawId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(eventDt, that.eventDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventDt);
    }
}

