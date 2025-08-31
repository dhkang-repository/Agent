package org.example.agent.global.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.JwtAuthenticationException;
import org.example.agent.global.security.TokenEncryptService;
import org.example.agent.global.security.response.TokenResponse;
import org.example.agent.global.util.AuthInfoLoggingFunction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_HEADER = "ACCESS_TOKEN";
    private static final String REFRESH_HEADER = "REFRESH_TOKEN";

    private final TokenEncryptService tokenEncryptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        TokenResponse token = resolveToken(request);
        String accessToken = nullIfBlank(token.accessToken());
        String refreshToken = nullIfBlank(token.refreshToken());

        try {
            // 1) Access 토큰 정상 → 인증 세팅
            if (accessToken != null && tokenEncryptService.validateToken(accessToken)) {
                authenticateWith(accessToken);
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_EXPIRED);
            request.setAttribute("url", request.getRequestURL());
            throw new JwtAuthenticationException.JwtExpiredValid(e);
        } catch (Exception e) {
            // 3) 기타 토큰 문제
            request.setAttribute("url", request.getRequestURL());
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_NOT_VALID);
            throw new JwtAuthenticationException.JwtTokenNotValid(e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Refresh 토큰 검증 후 새 토큰 발급 및 인증 컨텍스트/헤더 갱신
     */
    private boolean validRefreshToken(String refreshToken, HttpServletResponse response) {
        if (!StringUtils.hasText(refreshToken)) return false;

        try {
            return tokenEncryptService.validateToken(refreshToken);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 주어진 access 토큰으로 Spring Security 컨텍스트 세팅
     */
    private void authenticateWith(String accessToken) {
        Authentication authentication = tokenEncryptService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthInfoLoggingFunction.logAuthenticationAndGetUserId(authentication);
    }

    /**
     * 요청 헤더에서 토큰 정보 추출
     */
    private TokenResponse resolveToken(HttpServletRequest request) {
        try {
            String access = Objects.requireNonNull(nullIfBlank(request.getHeader(ACCESS_HEADER))).replace("Bearer ", "");
            String refresh = Objects.requireNonNull(nullIfBlank(request.getHeader(REFRESH_HEADER))).replace("Bearer ", "");;

            return TokenResponse.builder()
                    .accessToken(access)
                    .refreshToken(refresh)
                    .build();
        } catch (Exception e) {
            return TokenResponse.builder().build();
        }
    }

    private String nullIfBlank(String s) {
        return StringUtils.hasText(s) ? s : null;
    }
}
