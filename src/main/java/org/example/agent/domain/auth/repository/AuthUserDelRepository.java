package org.example.agent.domain.auth.repository;

import org.example.agent.entity.auth.AuthUserDelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserDelRepository extends JpaRepository<AuthUserDelEntity, Long> {
}
