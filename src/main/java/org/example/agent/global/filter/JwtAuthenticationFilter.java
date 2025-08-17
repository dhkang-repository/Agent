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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenEncryptService tokenEncryptService;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        String token = resolveToken(request);

        try {
            if (token != null && tokenEncryptService.validateToken(token)) {
                Authentication authentication = tokenEncryptService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                AuthInfoLoggingFunction.logAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_EXPIRED);
            request.setAttribute("url", request.getRequestURL());
            throw new JwtAuthenticationException.JwtExpiredValid(e);
        } catch (Exception e) {
            request.setAttribute("url", request.getRequestURL());
            request.setAttribute("exception", ErrorCode.ACCESS_TOKEN_NOT_VALID);
            throw new JwtAuthenticationException.JwtTokenNotValid(e);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

