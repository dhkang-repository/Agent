package org.example.agent.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.example.agent.global.security.response.TokenResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TokenIssueFunction {

    private static final String ACCESS_HEADER = "ACCESS_TOKEN";
    private static final String REFRESH_HEADER = "REFRESH_TOKEN";

    public static void issueToken(TokenResponse tokenResponse,
                                  HttpServletResponse response) {

        response.setHeader(ACCESS_HEADER, tokenResponse.accessToken());
        response.setHeader(REFRESH_HEADER, tokenResponse.refreshToken());

    }

}
