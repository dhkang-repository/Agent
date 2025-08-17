package org.example.agent.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.config.PersistenceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EntityScan(basePackages = "org.example.agent.entity")
@EnableJpaRepositories(
        basePackages = {"org.example.agent.domain", "org.example.agent.global.repository"},
        entityManagerFactoryRef = "agentEntityManagerFactory",
        transactionManagerRef = "agentTransactionManager")
@ComponentScan
public class AgentDbConfig {

    @Bean(name = "agentDatasource")
    public DataSource jpaDataSource(@Autowired AgentDatasourceProperties properties) throws Exception {
        log.info("properties:agentdb >> "+ properties.toString());
        try {
            Class<?> aClass = Class.forName(properties.driverClassName());
            log.info("properties:agentdb >> " + aClass.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.url());
        hikariConfig.setUsername(properties.username());
        hikariConfig.setPassword(properties.password());
        hikariConfig.setMaximumPoolSize(properties.maximumPoolSize());
        hikariConfig.setMinimumIdle(properties.minimumIdle());
        hikariConfig.setConnectionTimeout(properties.connectionTimeout());
        hikariConfig.setMaxLifetime(properties.maxLifetime());
        hikariConfig.setPoolName("agent_db");
        hikariConfig.setConnectionInitSql("SET SESSION block_encryption_mode = 'aes-256-ecb'");
        hikariConfig.setConnectionInitSql("");
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "agentEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean agentEntityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            PersistenceProperties jpaProperties
            ) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.example.agent.entity");
        factory.setDataSource(dataSource);
        factory.setPersistenceUnitName("agentEntityManager");

        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none"); // DDL 자동 생성 옵션: none, validate, update, create, create-drop
        properties.put("hibernate.show_sql", jpaProperties.isShowSql());
        properties.put("hibernate.format_sql", jpaProperties.isFormatSql());
        properties.put("hibernate.dialect", jpaProperties.getDialect());
        properties.put("hibernate.use_sql_comments", jpaProperties.isUseSqlComments());
        properties.put("hibernate.jdbc.time_zone", jpaProperties.getTimeZone());

        factory.setJpaProperties(properties);

        return factory;
    }

    @Primary
    @Bean(name = "agentTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("agentEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf.getObject());
        return txManager;
    }

}
