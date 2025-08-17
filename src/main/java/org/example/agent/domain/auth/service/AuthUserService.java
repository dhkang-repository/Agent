package org.example.agent.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.dto.AuthUserDto;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.DefineException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthUserRepository authUserRepository;

    public AuthUserEntity findById(Long id) {
        return authUserRepository.findById(id).orElseThrow(
                () -> new DefineException(ErrorCode.ENTITY_NOT_EXIST, "회원이 존재하지 않습니다.")
        );
    }

    public AuthUserEntity findByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseThrow(
                () -> new DefineException(ErrorCode.ENTITY_NOT_EXIST, "회원이 존재하지 않습니다.")
        );
    }

    @Transactional
    public void save(AuthUserEntity authUserEntity) {
        authUserRepository.save(authUserEntity);
    }

    @Transactional
    public void join(AuthUserDto dto) {
        authUserRepository.findByEmail(dto.getEmail()).ifPresent(authUser -> {
            throw new DefineException(ErrorCode.ENTITY_EXIST, "회원이 이미 존재합니다.");
        });

        authUserRepository.save(dto.toEntity());
    }

}

