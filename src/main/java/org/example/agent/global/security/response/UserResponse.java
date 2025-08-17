package org.example.agent.global.security.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.RoleTypeEnum;

public record UserResponse(
        @Schema(description = "유저 ID")
        Long userId,
        @Schema(description = "이름")
        String name,
        @Schema(description = "암호")
        String password,
        @Schema(description = "이메일")
        String email,
        @Schema(description = "권한")
        RoleTypeEnum role
) {
    public static UserResponse toUserResponse(AuthUserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getRoleType()
        );
    }
}
