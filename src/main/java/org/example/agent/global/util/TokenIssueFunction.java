package org.example.agent.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.example.agent.global.security.response.TokenResponse;
import org.springframework.http.HttpHeaders;

public class TokenIssueFunction {

    private static final String REFRESH_HEADER = "Refresh-Token";

    public static void issueToken(TokenResponse tokenResponse,
                                  HttpServletResponse response) {

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.accessToken());
        response.setHeader(REFRESH_HEADER, tokenResponse.refreshToken());
    }

}
