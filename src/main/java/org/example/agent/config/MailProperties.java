package org.example.agent.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ToString
@Component
@Data
@ConfigurationProperties(prefix = "mail")
@PropertySource(value = "classpath:mail/mail-${spring.profiles.active:local}.yml", factory = YamlPropertySourceFactory.class)
public class MailProperties {
    private String host;
    private String protocol;
    private Integer port;
    private String username;
    private String password;
    private String auth;
    private String enable;
    private String debug;
    private String defaultEncoding;
}
