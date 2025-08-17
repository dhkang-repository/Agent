package org.example.agent.domain.auth.repository;

import org.example.agent.entity.auth.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
    Optional<AuthTokenEntity> findByAuthUserId(Long id);
}
