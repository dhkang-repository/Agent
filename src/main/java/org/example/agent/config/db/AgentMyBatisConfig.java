package org.example.agent.config.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "org.example.agent.global.mybatis",
        sqlSessionFactoryRef = "agentSqlSessionFactory",
        sqlSessionTemplateRef = "agentSqlSessionTemplate"
)
public class AgentMyBatisConfig {

    @Primary
    @Bean
    @Qualifier("agentSqlSessionFactory")
    public SqlSessionFactory agentSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setDataSource(dataSource);
        // mapper.xml 의 resultType 패키지 주소 생략
        sqlSessionFactoryBean.setTypeAliasesPackage("org.example.agent.domain.common.dto");
        // mybatis 설정 파일 세팅
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:db/mybatis-config.xml"));
        // mapper.xml 위치 패키지 주소
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:db/mapper/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean
    @Qualifier("agentSqlSessionTemplate")
    public SqlSessionTemplate agentSqlSessionTemplate(@Qualifier("agentSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
