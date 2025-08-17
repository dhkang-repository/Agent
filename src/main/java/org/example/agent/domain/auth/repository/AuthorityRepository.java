package org.example.agent.domain.auth.repository;

import org.example.agent.entity.auth.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    Optional<AuthorityEntity> findByType(String authType);
}
