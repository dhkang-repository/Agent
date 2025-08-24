package org.example.agent.config;


import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"); // docker-compose 기준
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic geoLocationsTopic() {
        // 이름, 파티션 수, replication-factor
        return new NewTopic("geo.locations.v1", 3, (short) 1);
    }

}
