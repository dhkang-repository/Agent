package org.example.agent.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.agent.config.JwtEncryptProperties;
import org.example.agent.domain.auth.repository.AuthTokenRepository;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.example.agent.entity.auth.AuthTokenEntity;
import org.example.agent.entity.auth.AuthUserEntity;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.exception.JwtAuthenticationException;
import org.example.agent.global.security.response.TokenResponse;
import org.example.agent.global.security.response.UserResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenEncryptService {

    private final JwtEncryptProperties jwtEncryptProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;
    private final AuthUserRepository authUserRepository;

    /**
     * 패스워드 인코더 암호화
     * @param before: 원본 암호
     */
    public String encrypt(String before) {
        return passwordEncoder.encode(before);
    }

    /**
     * 패스워드 인코더 일치 여부
     * @param rawPassword: 원본 암호
     * @param encodedPassword: 인코딩 암호
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 토큰 기반 유저 정보 취득
     * @param accessToken: 토큰
     * @return
     */
    public UserResponse findUserByAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Object userId = claims.get("userId");

        if (ObjectUtils.isEmpty(userId)) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        return authUserRepository.findById(Long.valueOf(userId.toString()))
                .map(UserResponse::toUserResponse)
                .orElseThrow(() -> new DefineException(ErrorCode.ENTITY_NOT_EXIST));
    }

    /**
     * 토큰 유효성 검증
     * @param accessToken: 토큰
     * @return
     */
    public Boolean validateToken(String accessToken) {

        Jwts.parser()
                .setSigningKey(jwtEncryptProperties.getSecretKey())
                .build()
                .parseClaimsJws(accessToken);
        return true;

    }

    /**
     * 토큰 정보 취득
     * @param accessToken: 토큰
     * @return
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtEncryptProperties.getSecretKey())
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 토큰 생성
     * @param userId: 유저 식별자
     * @return
     */
    @Transactional
    public TokenResponse createNewToken(Long userId) {
        String accessToken = getToken(userId, Duration.ofSeconds(jwtEncryptProperties.getExpireAccessTokenSecond()));
        String refreshToken = getToken(userId, Duration.ofSeconds(jwtEncryptProperties.getExpireRefreshTokenSecond()));

        AuthTokenEntity authTokenEntity = AuthTokenEntity.of(
                passwordEncoder.encode(accessToken),
                passwordEncoder.encode(refreshToken),
                userId);

        authTokenRepository.save(authTokenEntity);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 갱신
     * @param userId: 유저 식별자
     * @return
     */
    @Transactional
    public TokenResponse updateNewToken(Long userId) {
        String accessToken = getToken(userId, Duration.ofSeconds(jwtEncryptProperties.getExpireAccessTokenSecond()));
        String refreshToken = getToken(userId, Duration.ofSeconds(jwtEncryptProperties.getExpireRefreshTokenSecond()));

        AuthTokenEntity authTokenEntity = authTokenRepository.findByAuthUserId(userId)
                .orElseThrow(() -> new DefineException(ErrorCode.ENTITY_NOT_EXIST));

        authTokenEntity.updateToken(
                passwordEncoder.encode(accessToken),
                passwordEncoder.encode(refreshToken)
        );

        authTokenRepository.save(authTokenEntity);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 생성 및 갱신
     * @param userId: 유저 식별자
     * @return
     */
    @Transactional
    public TokenResponse upsertToken(Long userId) {
        return authTokenRepository.findByAuthUserId(userId)
                .map(token -> updateNewToken(userId))
                .orElseGet(() -> createNewToken(userId));
    }

    /**
     * 토큰 재발급
     * @param accessToken: 액세스 토큰
     * @param refreshToken: 레프레시 토큰
     * @return
     */
    @Transactional
    public TokenResponse reissueToken(String accessToken, String refreshToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(jwtEncryptProperties.getSecretKey())
                .build()
                .parseClaimsJws(accessToken);

        Long userId = (Long) claimsJws.getPayload().get("userId");
        Optional<AuthTokenEntity> byAuthUserEntityId = authTokenRepository.findByAuthUserId(userId);
        if (byAuthUserEntityId.isEmpty()) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }

        AuthTokenEntity authTokenEntity = byAuthUserEntityId.get();
        if (!authTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 유효하지 않습니다.");
        }

        return updateNewToken(userId);
    }

    /**
     * REFRESH TOKEN 기반 운영자 식별자 취득
     * @param refreshToken
     * @return
     */
    public Long getUserIdByRefreshToken(String refreshToken) {
        Long userId;
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(jwtEncryptProperties.getSecretKey())
                    .build()
                    .parseClaimsJws(refreshToken);

            userId = Long.valueOf(claimsJws.getPayload().get("userId").toString());
        } catch (ExpiredJwtException e) {
            throw new DefineException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        } catch (Exception e) {
            throw new DefineException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        return userId;
    }

    /**
     * 키 취득
     * @return
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtEncryptProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰 생성
     * @param id: 식별자
     * @param expireAt: 만료시간
     * @return
     */
    private String getToken(Long id, Duration expireAt) {
        Date now = new Date();
        Instant instant = now.toInstant();

        return Jwts.builder()
                .claim("userId", id)
                .issuedAt(now)
                .expiration(Date.from(instant.plus(expireAt)))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰 생성
     * @param id: 식별자
     * @return
     */
    public String getToken(Long id) {
        Date now = new Date();
        Instant instant = now.toInstant();

        return Jwts.builder()
                .claim("userId", id)
                .issuedAt(now)
                .expiration(Date.from(instant.plus(Duration.ofSeconds(jwtEncryptProperties.getExpireAccessTokenSecond()))))
                .signWith(getSigningKey())
                .compact();
    }


    /**
     * 토큰 기반 Authentication 취득
     * @param accessToken: 토큰
     * @return
     */
    public Authentication getAuthentication(String accessToken) {
        UserResponse userByAccessToken = findUserByAccessToken(accessToken);

        Optional<AuthTokenEntity> authEntityOptional = authTokenRepository.findById(userByAccessToken.userId());

        if(authEntityOptional.isPresent()) {
            AuthTokenEntity authTokenEntity = authEntityOptional.get();

            if(!passwordEncoder.matches(accessToken, authTokenEntity.getAccessToken())) {
                throw new JwtAuthenticationException.JwtTokenNotValid();
            }

            Optional<AuthUserEntity> authUserOptional = authUserRepository.findById(authTokenEntity.getAuthUserId());

            AuthUserEntity authUserEntity = authUserOptional.get();

            List<GrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + authUserEntity.getRoleType()));


            SecurityAuthUser principal = new SecurityAuthUser(
                    authUserEntity.getId(),
                    authUserEntity.getName(),
                    authUserEntity.getPassword(),
                    authUserEntity.getEmail(),
                    simpleGrantedAuthorities);

            return new UsernamePasswordAuthenticationToken(principal, authUserEntity.getId(), simpleGrantedAuthorities);
        }

        return null;
    }
}
