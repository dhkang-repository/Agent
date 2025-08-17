package org.example.agent.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.domain.auth.dto.AuthUserDto;
import org.example.agent.domain.auth.service.AuthUserService;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.annotation.WebAdapter;
import org.example.agent.global.constrant.LogMarker;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.security.SecurityAuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static org.example.agent.global.constrant.GlobalConst.BASE_URL;

@Tag(name = "운영자 API", description = "운영자 관련 기능 제공")
@Slf4j
@WebAdapter
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthUserService authUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * @apiNote 등록 API
     */
    @Operation(
            summary = "사용자 등록 API",
            description = "사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "", description = "성공",
                            content = @Content(schema = @Schema(
                                    implementation = ResponseResult.class
                            ))),
            }
    )
    @PostMapping("/user")
//    @PreAuthorize("hasAnyRole('SS', 'S1')")
    public ResponseEntity<?> insertAuthUsers(@Valid @RequestBody AuthUserDto.AuthUserJoinRequest request,
                                             @AuthenticationPrincipal SecurityAuthUser securityUser) {
        String encode = passwordEncoder.encode(request.getPassword());
        request.setPassword(encode);

        authUserService.join(request.toDto());

        log.info(LogMarker.SERVICE.getMarker(), "SecurityUser : {}", securityUser);
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        null
                )
        );
    }


    /**
     * @apiNote 조회 API
     */
    @Operation(
            summary = "사용자 조회 API",
            description = "사용자를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "", description = "성공",
                            content = @Content(schema = @Schema(
                                    implementation = ResponseResult.class
                            ))),
            }
    )
    @GetMapping("/user/me")
    @PreAuthorize("hasAnyRole('SS', 'L1')")
    public ResponseEntity<?> findAuthUsers(@AuthenticationPrincipal SecurityAuthUser securityUser) {

        AuthUserEntity authUserEntity = authUserService.findById(securityUser.getUserId());

        log.info(LogMarker.SERVICE.getMarker(), "SecurityUser : {}", securityUser);
        return ResponseEntity.ok().body(
                ResponseResult.of(
                        ResponseHeader.success(),
                        authUserEntity
                )
        );
    }
}
