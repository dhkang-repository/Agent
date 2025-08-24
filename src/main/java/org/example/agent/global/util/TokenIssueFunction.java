package org.example.agent.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.example.agent.config.JwtEncryptProperties;
import org.example.agent.global.security.response.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class TokenIssueFunction {

    public static void issueToken(TokenResponse tokenResponse,
                                  JwtEncryptProperties jwtEncryptProperties,
                                  HttpServletResponse response) {

        // HttpOnly 쿠키로 전달(보안/간편)
        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", tokenResponse.accessToken())
                .httpOnly(true).secure(true).sameSite("Lax").path("/")
                .maxAge(Duration.ofSeconds(jwtEncryptProperties.getExpireAccessTokenSecond())).build();

        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", tokenResponse.refreshToken())
                .httpOnly(true).secure(true).sameSite("Lax").path("/")
                .maxAge(Duration.ofSeconds(jwtEncryptProperties.getExpireRefreshTokenSecond())).build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
    }

}
