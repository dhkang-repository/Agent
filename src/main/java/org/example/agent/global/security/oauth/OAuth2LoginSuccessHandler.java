package org.example.agent.global.security.oauth;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.security.TokenEncryptService;
import org.example.agent.global.security.response.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenEncryptService tokenEncryptService;
    private final AuthUserRepository authUserRepository;

    @Value("${app.oauth2.success-redirect}")
    private String successRedirect;

    @Value("${app.oauth2.success-onboarding}")
    private String successOnboarding;

    // 쿠키 만료는 JWT 설정과 맞추세요
    private static final Duration ACCESS_MAX_AGE  = Duration.ofHours(1);
    private static final Duration REFRESH_MAX_AGE = Duration.ofDays(14);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        AuthUserEntity user = authUserRepository.findByEmail(email).orElseThrow();

        // 구글 picture → profile_image 초기화(최초 1회)
        if (user.getProfileImage() == null) {
            Object pic = oAuth2User.getAttributes().get("picture");
            if (pic instanceof String s && !s.isBlank()) {
                user.setProfileImage(s);
                authUserRepository.save(user);
            }
        }

        boolean needOnboarding =
                user.getNickname() == null
                        || user.getNickname().isBlank()
                        || user.getPhoneNumber() == null; // 정책에 맞게 필수값 지정

        String redirect = needOnboarding
                ? successRedirect  // 프론트 온보딩 페이지
                : successRedirect; // 프론트 온보딩 페이지

        // JWT 발급 + DB 저장 (AuthTokenEntity)
        TokenResponse tokens = tokenEncryptService.createNewToken(user.getId());

        // HttpOnly 쿠키로 전달(보안/간편)
        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", tokens.accessToken())
                .httpOnly(true).secure(true).sameSite("Lax").path("/")
                .maxAge(ACCESS_MAX_AGE).build();
        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", tokens.refreshToken())
                .httpOnly(true).secure(true).sameSite("Lax").path("/")
                .maxAge(REFRESH_MAX_AGE).build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        // 프론트로 리다이렉트
        response.sendRedirect(redirect);
    }
}
