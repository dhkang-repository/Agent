package org.example.agent.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ProviderEnum;
import org.example.agent.global.constrant.RoleTypeEnum;
import org.example.agent.global.security.response.TokenResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDto {
    private Long id;
    private String email;
    private String name;
    private String password;
    private RoleTypeEnum roleType;
    private Integer loginCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAuthDt;

    public AuthUserDto(AuthUserEntity entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.password = entity.getPassword();
        this.roleType = entity.getRoleType();
        this.loginCnt = entity.getLoginCnt();
        this.joinDt = entity.getJoinDt();
        this.lastAuthDt = entity.getLastAuthDt();
    }

    public static AuthUserDto from (AuthUserEntity entity) {
        return new AuthUserDto(entity);
    }

    public AuthUserEntity toEntity() {
        return AuthUserEntity.builder()
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .roleType(roleType)
                .loginCnt(0)
                .provider(ProviderEnum.IN)
                .locale("ko_KR")
                .timezone("Asia/Seoul")
                .joinDt(LocalDateTime.now())
                .lastAuthDt(LocalDateTime.now())
                .lastUpdateDt(LocalDateTime.now())
                .build();
    }

    @Data
    static public class AuthUserLoginRequest {
        @Schema(description = "이메일", example = "test@inavi.kr")
        @NotBlank(message = "필수입니다.")
        private String email;

        @Schema(description = "암호", example = "1234")
        @NotBlank(message = "필수입니다.")
        private String password;
    }


    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static public class AuthUserTokenResponse {
        @Schema(description = "email", example = "test@inavi.kr")
        private String email;
        @Schema(description = "name", example = "test")
        private String name;
        @Schema(description = "권한 타입", example = "S1")
        private RoleTypeEnum roleType;
        @Schema(description = "access token", example = "asdf....")
        private String accessToken;
        @Schema(description = "refresh token", example = "asdf....")
        private String refreshToken;
        @Schema(description = "최근 로그인 일시", example = "yyyy-MM-dd HH:mm")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastAuthDt;

        public static AuthUserTokenResponse from (AuthUserEntity authUserEntity,
                                                  TokenResponse newToken) {
            return AuthUserDto.AuthUserTokenResponse.builder()
                    .email(authUserEntity.getEmail())
                    .name(authUserEntity.getName())
                    .roleType(authUserEntity.getRoleType())
                    .accessToken(newToken.accessToken())
                    .refreshToken(newToken.refreshToken())
                    .lastAuthDt(authUserEntity.getLastAuthDt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static public class AuthUserTokenRefreshRequest {
        @Schema(description = "Access Token", example = "abcd...")
        @NotBlank(message = "필수입니다.")
        private String accessToken;

        @Schema(description = "Refresh Token", example = "abcd...")
        @NotBlank(message = "필수입니다.")
        private String refreshToken;
    }


    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static public class AuthUserJoinRequest {
        @Schema(description = "권한", example = "S1")
        @NotBlank(message = "필수입니다.")
        private String roleType;

        @Schema(description = "이름", example = "test name")
        @NotBlank(message = "필수입니다.")
        private String name;

        @Schema(description = "이메일", example = "test@inavi.kr")
        @NotBlank(message = "필수입니다.")
        private String email;

        @Schema(description = "암호", example = "1234")
        private String password = "asdf1234@@!!";

        public AuthUserDto toDto() {
            return AuthUserDto.builder()
                    .roleType(RoleTypeEnum.parsing(roleType))
                    .name(name)
                    .email(email)
                    .password(password)
                    .build();
        }
    }
}