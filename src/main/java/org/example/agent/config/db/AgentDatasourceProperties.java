package org.example.agent.config.db;

import lombok.Setter;
import lombok.ToString;
import org.example.agent.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ToString
@Component
@Setter
@ConfigurationProperties(prefix = "jpa.datasource.agent")
@PropertySource(value = "classpath:db/db-${spring.profiles.active:local}.yml", factory = YamlPropertySourceFactory.class)
public class AgentDatasourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int connectionTimeout;
    private int validationTimeout;
    private int maxLifetime;
    private int minimumIdle;
    private int maximumPoolSize;
    private String initializationMode;

    public String url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String driverClassName() {
        return driverClassName;
    }

    public int connectionTimeout() {
        return connectionTimeout;
    }

    public int validationTimeout() {
        return validationTimeout;
    }

    public int minimumIdle() {
        return minimumIdle;
    }

    public int maximumPoolSize() {
        return maximumPoolSize;
    }

    public String initializationMode() {
        return initializationMode;
    }

    public int maxLifetime() {
        return maxLifetime;
    }
}
