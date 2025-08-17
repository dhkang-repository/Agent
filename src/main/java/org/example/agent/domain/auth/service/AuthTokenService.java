package org.example.agent.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.repository.AuthTokenRepository;
import org.example.agent.entity.auth.AuthTokenEntity;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.DefineException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenEntity findById(Long id) {
        return authTokenRepository.findById(id).orElseThrow(
                () -> new DefineException(ErrorCode.ENTITY_NOT_EXIST, "회원 토큰이 존재하지 않습니다.")
        );
    }

    @Transactional
    public void delete(Long userId) {
        AuthTokenEntity tokenEntity = findById(userId);
        authTokenRepository.delete(tokenEntity);
    }

}

