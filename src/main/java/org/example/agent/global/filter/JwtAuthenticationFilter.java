package org.example.agent.global.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.agent.config.JwtEncryptProperties;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.JwtAuthenticationException;
import org.example.agent.global.security.TokenEncryptService;
import org.example.agent.global.security.response.TokenResponse;
import org.example.agent.global.util.AuthInfoLoggingFunction;
import org.example.agent.global.util.TokenIssueFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_COOKIE = "ACCESS_TOKEN";
    private static final String REFRESH_COOKIE = "REFRESH_TOKEN";

    private final TokenEncryptService tokenEncryptService;
    private final JwtEncryptProperties jwtEncryptProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        TokenResponse token = resolveToken(request); // header 우선, 없으면 쿠키
        String accessToken = nullIfBlank(token.accessToken());
        String refreshToken = nullIfBlank(token.refreshToken());

        try {
            // 1) Access 토큰 정상 → 인증 세팅
            if (accessToken != null && tokenEncryptService.validateToken(accessToken)) {
                authenticateWith(accessToken);
            }
        } catch (ExpiredJwtException e) {
            // 2) Access 만료 → Refresh로 재발급 시도
            boolean refreshed = tryRefreshAndAuthenticate(refreshToken, response);
            if (!refreshed) {
                // 재발급 실패 시에만 예외 처리
                request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_EXPIRED);
                request.setAttribute("url", request.getRequestURL());
                throw new JwtAuthenticationException.JwtExpiredValid(e);
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
            // 재발급 성공했으면 예외 없이 계속 진행
        } catch (Exception e) {
            // 3) 기타 토큰 문제
            request.setAttribute("url", request.getRequestURL());
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_NOT_VALID);
            throw new JwtAuthenticationException.JwtTokenNotValid(e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Refresh 토큰 검증 후 새 토큰 발급 및 인증 컨텍스트/쿠키/헤더 갱신
     */
    private boolean tryRefreshAndAuthenticate(String refreshToken, HttpServletResponse response) {
        if (!StringUtils.hasText(refreshToken)) return false;

        try {
            if (!tokenEncryptService.validateToken(refreshToken)) return false;

            // refresh 토큰의 소유자 식별
            Authentication authFromRefresh = tokenEncryptService.getAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authFromRefresh);
            Long userId = AuthInfoLoggingFunction.logAuthenticationAndGetUserId(authFromRefresh);

            // 새 토큰 발급 (필요 시 refresh도 회전)
            TokenResponse newToken = tokenEncryptService.createNewToken(userId);
            if (newToken == null || !StringUtils.hasText(newToken.accessToken())) return false;

            // 응답 쿠키 갱신
            TokenIssueFunction.issueToken(newToken, jwtEncryptProperties, response);

            // (선택) Authorization 헤더도 갱신 → 다운스트림에서 헤더만 읽는 경우 대비
            response.setHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + newToken.accessToken());

            return true;
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
     * 헤더 우선 → 쿠키 fallback
     */
    private TokenResponse resolveToken(HttpServletRequest request) {
        String accessFromHeader = resolveAccessFromHeader(request);
        String accessFromCookie = resolveCookie(request, ACCESS_COOKIE);
        String refreshFromCookie = resolveCookie(request, REFRESH_COOKIE);

        String access = StringUtils.hasText(accessFromHeader) ? accessFromHeader : accessFromCookie;
        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refreshFromCookie)
                .build();
    }

    private String resolveAccessFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String resolveCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return nullIfBlank(c.getValue());
            }
        }
        return null;
    }

    private String nullIfBlank(String s) {
        return StringUtils.hasText(s) ? s : null;
    }
}
