package org.example.agent.global.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.security.TokenEncryptService;
import org.example.agent.global.security.response.TokenResponse;
import org.example.agent.global.util.TokenIssueFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenEncryptService tokenEncryptService;
    private final AuthUserRepository authUserRepository;

    @Value("${app.oauth2.success-redirect}")
    private String successRedirect;

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

        // JWT 발급 + DB 저장 (AuthTokenEntity)
        TokenResponse tokens = tokenEncryptService.createNewToken(user.getId());

        TokenIssueFunction.issueToken(tokens, response);

        // 3) 앱 딥링크로 리다이렉트 - fragment 방식 (서버/프록시 로그에 덜 남음)
        String redirectUri = UriComponentsBuilder.fromUriString(successRedirect)
                .fragment("accessToken={at}&refreshToken={rt}")
                .buildAndExpand(tokens.accessToken(), tokens.refreshToken())
                .toUriString();

        // 4) 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
