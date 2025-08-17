package org.example.agent.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ToString
@Component
@Data
@ConfigurationProperties(prefix = "jpa.persistence")
@PropertySource(value = "classpath:db/db-${spring.profiles.active:local}.yml", factory = YamlPropertySourceFactory.class)
public class PersistenceProperties {
    private String database;
    private String dialect;
    private boolean showSql;
    private boolean formatSql;
    private boolean useSqlComments;
    private boolean openInView;
    private String hbm2ddlAuto;
    private int defaultBatchFetchSize;
    private String timeZone;
}
