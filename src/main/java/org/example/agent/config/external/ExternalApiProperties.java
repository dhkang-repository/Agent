package org.example.agent.config.external;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.agent.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@ToString
@Configuration
@PropertySource(value = {"classpath:external/api-${spring.profiles.active:local}.yml"}, factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "api")
public class ExternalApiProperties {
    private String url;
    private String key;
    private String value;

    private MemberProperty member;
    private BiscuitProcessProperty biscuit;
    private PushProcessProperty push;


    @Setter
    @Getter
    @NoArgsConstructor
    public static class MemberProperty {
        private String active;
        private String suspend;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class BiscuitProcessProperty {
        private String process;
    }


    @Setter
    @Getter
    @NoArgsConstructor
    public static class PushProcessProperty {
        private String reserve;
        private String delete;
    }


}
