package org.example.agent.global.security;

import jakarta.servlet.http.HttpServletResponse;

public interface CustomCorsHeader {
    default void addHeaders(final String origin, final HttpServletResponse response) {
        // CORS 헤더 수동 추가
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*"); // 필요 시 제한적으로
            response.setHeader("Access-Control-Allow-Credentials", "false");
        }

        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, TRACE");

    }
}
