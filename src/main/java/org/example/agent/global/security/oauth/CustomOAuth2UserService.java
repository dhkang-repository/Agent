// src/main/java/org/example/agent/global/security/oauth/CustomOAuth2UserService.java
package org.example.agent.global.security.oauth;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ProviderEnum;
import org.example.agent.global.constrant.RoleTypeEnum;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User delegateUser = new DefaultOAuth2UserService().loadUser(req);

        String registrationId = req.getClientRegistration().getRegistrationId(); // "google", "naver" ...

        ProviderEnum provider  = mapToProvider(registrationId);                   // GL, NV, (or EXCEPTION)

        Map<String, Object> attrs = delegateUser.getAttributes();

        // 구글은 sub가 고유 ID
        String providerId = (String) attrs.get("sub");          // naver면 id 또는 response.id 등 매핑 필요
        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException("providerId(sub)이 없습니다.");
        }

        String email = (String) attrs.get("email");
        String name  = (String) attrs.getOrDefault("name", email != null ? email : "USER");

        // 1) provider + providerId로 1차 조회
        Optional<AuthUserEntity> bySocial = authUserRepository.findByProviderAndProviderId(provider, providerId);

        AuthUserEntity user = bySocial.orElseGet(() -> {
            // 2) 동일 email 계정 있으면 연결(link)
            if (email != null && !email.isBlank()) {
                Optional<AuthUserEntity> byEmail = authUserRepository.findByEmail(email);
                if (byEmail.isPresent()) {
                    AuthUserEntity u = byEmail.get();
                    // 기존 로컬/다른 상태였어도 이번에 소셜과 연결
                    u.linkProvider(provider, providerId);
                    u.setLastAuthDt(LocalDateTime.now());
                    // 필요 시 name 등도 최신화
                    if (u.getName() == null || u.getName().isBlank()) u.setName(name);
                    return authUserRepository.save(u);
                }
            }

            // 3) 신규 자동가입
            AuthUserEntity neo = (AuthUserEntity) AuthUserEntity.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 임시
                    .roleType(RoleTypeEnum.L1)
                    .locale("ko_KR")
                    .timezone("Asia/Seoul")
                    .lastAuthDt(LocalDateTime.now())
                    .joinDt(LocalDateTime.now())
                    .lastUpdateDt(LocalDateTime.now())
                    .build();

            neo.linkProvider(provider, providerId);

            return authUserRepository.save(neo);
        });

        user.setLastAuthDt(LocalDateTime.now());
        authUserRepository.save(user);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleType().name()));

        return new DefaultOAuth2User(authorities, attrs, "sub");
    }

    private ProviderEnum mapToProvider(String registrationId) {
        if (registrationId == null) throw new OAuth2AuthenticationException("registrationId 없음");

        switch (registrationId.toLowerCase()) {
            case "google": return ProviderEnum.GL;
            case "naver":  return ProviderEnum.NV;
            default:
                // ProviderEnum.parsing(...)은 UN이면 예외를 던지므로 동일한 정책 유지
                throw new OAuth2AuthenticationException("지원하지 않는 provider: " + registrationId);
        }
    }
}
