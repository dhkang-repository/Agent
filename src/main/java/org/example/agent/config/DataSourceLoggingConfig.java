package org.example.agent.config;

import jakarta.annotation.PostConstruct;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceLoggingConfig {

    @PostConstruct
    void init() {
        // 필요 시 부팅 로그로 현재 설정 확인
        org.apache.logging.log4j.LogManager.getLogger(getClass())
                .info("Datasource-Proxy JDBC logging is ENABLED");
    }


    /**
     * 기존 AutoConfig 된 DataSource를 프록시로 감싸 Primary로 다시 노출
     */
    @Bean(name = "dataSource")
    @Primary
    public DataSource loggingDataSource(@Autowired @Qualifier("agentDatasource") DataSource dataSource) {
        // 1) 일반 쿼리 로깅 (SQL + 바인딩 파라미터)
        SLF4JQueryLoggingListener queryLogger = new SLF4JQueryLoggingListener();
        queryLogger.setQueryLogEntryCreator(new CustomParameterTransformer());
        queryLogger.setLogLevel(SLF4JLogLevel.INFO); // INFO로 남김

        return ProxyDataSourceBuilder
                .create(dataSource)
                .name("MyDS-Logging")  // 로그 식별 이름
                .listener(queryLogger)
                .countQuery() // 실행 건수/배치 카운트 포함
                .build();
    }

}
