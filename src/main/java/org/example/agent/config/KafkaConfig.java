package org.example.agent.config;

import org.example.agent.domain.geo.dto.GeoReportDto;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Profile("home")
@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, GeoReportDto> kafkaTemplate(ProducerFactory<String, GeoReportDto> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ProducerFactory<String, GeoReportDto> producerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildProducerProperties());
        // (필요 시 추가 옵션 주입)
        return new DefaultKafkaProducerFactory<>(cfg);
    }

}

