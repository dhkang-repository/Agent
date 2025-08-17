package org.example.agent.domain.auth.repository;

import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ProviderEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {
    Optional<AuthUserEntity> findByEmail(String email);
    Optional<AuthUserEntity> findByProviderAndProviderId(ProviderEnum provider, String providerId);
}
