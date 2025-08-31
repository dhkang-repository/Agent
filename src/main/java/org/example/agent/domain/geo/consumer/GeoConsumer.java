package org.example.agent.domain.geo.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.geo.dto.GeoReportDto;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Profile("home")
@Component
@RequiredArgsConstructor
public class GeoConsumer {
    private final StringRedisTemplate redis;
    private final ObjectMapper om;

    @KafkaListener(topics = "geo.locations.v1", groupId = "geo-cache")
    public void onMessage(GeoReportDto r) throws JsonProcessingException {
        // 최신 위치 캐시(예: 5분 TTL)
        String key = "geo:last:" + r.userId();
        redis.opsForValue().set(key, om.writeValueAsString(r), Duration.ofMinutes(5));
    }
}

