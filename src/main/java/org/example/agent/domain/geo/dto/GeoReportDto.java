package org.example.agent.domain.geo.dto;

public record GeoReportDto(
    String userId,
    double lat,
    double lon,
    Double acc,
    Double heading,
    Double speed,
    long ts,
    String seqId
) {
}
