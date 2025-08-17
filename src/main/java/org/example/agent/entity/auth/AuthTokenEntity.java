package org.example.agent.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = false)
@Table(name = "tbl_auth_token", schema = "agent_db", catalog = "agent_db")
@DynamicUpdate
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthTokenEntity {
    @Comment("식별자")
    @Id
    @Column(name = "auth_user_id")
    private Long authUserId;

    @Setter
    @Comment("최근 발급 ACCESS TOKEN")
    @Column(name = "access_token")
    private String accessToken;

    @Comment("최근 발급 REFRESH TOKEN")
    @Column(name = "refresh_token")
    private String refreshToken;

    @Comment("최근 변경 일시")
    @LastModifiedDate

    @Column(name = "update_dt")
    private LocalDateTime lastUpdateDt;

    private AuthTokenEntity(String accessToken, String refreshToken, Long authUserId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authUserId = authUserId;
    }

    public static AuthTokenEntity of(String accessToken, String refreshToken, Long authUserId) {
        return new AuthTokenEntity(accessToken, refreshToken, authUserId);
    }

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
