package org.example.agent.global.security.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TokenResponse(
        @Schema(description = "Access Token")
        String accessToken,
        @Schema(description = "Refresh Token")
        String refreshToken) {
}