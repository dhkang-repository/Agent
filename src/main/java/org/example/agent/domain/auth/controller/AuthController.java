package org.example.agent.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.annotation.WebAdapter;
import org.example.agent.domain.auth.dto.AuthUserDto;
import org.example.agent.domain.auth.service.AuthTokenService;
import org.example.agent.domain.auth.service.AuthUserService;
import org.example.agent.entity.auth.AuthTokenEntity;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.security.SecurityAuthUser;
import org.example.agent.global.security.TokenEncryptService;
import org.example.agent.global.security.response.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.example.agent.global.constrant.GlobalConst.BASE_URL;

@Slf4j
@WebAdapter
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;
    private final TokenEncryptService tokenEncryptService;
    private final AuthTokenService authTokenService;

    /**
     * @apiNote 로그인 인증 API
     * @return
     */
    @Operation(
            summary = "로그인 인증 API",
            description = "이메일과 비밀번호를 기반으로 사용자를 인증합니다.",
            responses = {
                    @ApiResponse(responseCode = "", description = "성공",
                            content = @Content(schema = @Schema(
                                    implementation = AuthUserDto.AuthUserTokenResponse.class
                            ))),
            }
    )
    @PostMapping("/auth/login")
    @Transactional
    public ResponseEntity<ResponseResult<AuthUserDto.AuthUserTokenResponse>> authenticate(
            @RequestBody @Valid AuthUserDto.AuthUserLoginRequest request){

        AuthUserEntity authUserEntity = authUserService.findByEmail(request.getEmail());

        log.info("{}", authUserEntity);

        if(!tokenEncryptService.matches(request.getPassword(), authUserEntity.getPassword())){
            throw new DefineException(ErrorCode.PASSWORD_NOT_VALID);
        }

        return prcessAndGetResponseResultResponseEntity(authUserEntity);
    }


    private ResponseEntity<ResponseResult<AuthUserDto.AuthUserTokenResponse>> prcessAndGetResponseResultResponseEntity(AuthUserEntity authUserEntity) {

        TokenResponse newToken = tokenEncryptService.upsertToken(authUserEntity.getId());

        authUserEntity.setLastAuthDt(LocalDateTime.now());
        authUserEntity.setLoginCnt(authUserEntity.getLoginCnt()+1);

        authUserService.save(authUserEntity);

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        AuthUserDto.AuthUserTokenResponse.from(authUserEntity, newToken)
                )
        );
    }


    /**
     * @apiNote AccessToken 재발행 API
     * @return
     */
    @Operation(
            summary = "Token 재발행 API",
            description = "Token을 재발행 합니다.",
            responses = {
                    @ApiResponse(responseCode = "", description = "성공",
                            content = @Content(schema = @Schema(
                                    implementation = AuthUserDto.AuthUserTokenResponse.class
                            ))),
            }
    )
    @PostMapping("/auth/refresh")
    @Transactional
    public ResponseEntity<ResponseResult<AuthUserDto.AuthUserTokenResponse>> refreshToken(@RequestBody @Valid AuthUserDto.AuthUserTokenRefreshRequest request){
        Long userId = tokenEncryptService.getUserIdByRefreshToken(request.getRefreshToken());

        AuthUserEntity authUserEntity = authUserService.findById(userId);

        AuthTokenEntity tokenEntity = authTokenService.findById(authUserEntity.getId());

        if(!tokenEncryptService.matches(request.getRefreshToken(), tokenEntity.getRefreshToken())){
            throw new DefineException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }

        if(!tokenEncryptService.matches(request.getAccessToken(), tokenEntity.getAccessToken())){
            throw new DefineException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }

        TokenResponse tokenResponse = tokenEncryptService.updateNewToken(authUserEntity.getId());

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        AuthUserDto.AuthUserTokenResponse.from(authUserEntity, tokenResponse)
                )
        );
    }

    /**
     * @apiNote 로그아웃 API
     * @return
     */
    @Operation(
            summary = "로그아웃 API",
            description = "로그아웃하여 발급된 TOKEN을 만료합니다.",
            responses = {
                    @ApiResponse(responseCode = "", description = "성공",
                            content = @Content(schema = @Schema(
                                    implementation = ResponseResult.class
                            ))),
            }
    )
    @PostMapping("/auth/logout")
    @Transactional
    @PreAuthorize("hasAnyRole('SS', 'S1', 'G1', 'G2')")
    public ResponseEntity<ResponseResult> logout(@AuthenticationPrincipal SecurityAuthUser securityAuthUser){

        authTokenService.delete(securityAuthUser.getUserId());

        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        null
                )
        );
    }

}
