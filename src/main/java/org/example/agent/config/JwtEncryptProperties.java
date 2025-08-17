package org.example.agent.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ToString
@Component
@Data
@ConfigurationProperties(prefix = "jwt")
@PropertySource(value = "classpath:jwt/jwt-${spring.profiles.active:local}.yml", factory = YamlPropertySourceFactory.class)
public class JwtEncryptProperties {
    private String secretKey;
    private int expireAccessTokenSecond;
    private int expireRefreshTokenSecond;
}
