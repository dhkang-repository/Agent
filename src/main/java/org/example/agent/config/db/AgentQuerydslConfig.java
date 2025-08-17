package org.example.agent.config.db;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class AgentQuerydslConfig {
    @PersistenceContext(unitName = "agentEntityManager")
    private EntityManager entityManager;

    @Primary
    @Bean(name = "agentJPAQueryFactory")
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
