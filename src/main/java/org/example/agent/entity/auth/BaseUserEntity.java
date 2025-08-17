package org.example.agent.entity.auth;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.agent.global.constrant.ProviderEnum;
import org.example.agent.global.constrant.RoleTypeEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter @Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_user_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleTypeEnum roleType;

    @Column(name = "login_cnt", nullable = false)
    private int loginCnt = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 8, nullable = false)
    private ProviderEnum provider;

    @Column(name = "provider_id", length = 128)
    private String providerId;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "locale", length = 10)
    private String locale = "ko_KR";

    @Column(name = "timezone", length = 40)
    private String timezone = "Asia/Seoul";

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @Column(name = "mkt_opt_in", nullable = false)
    private boolean marketingOptIn = false;

    @Column(name = "svc_opt_in", nullable = false)
    private boolean serviceOptIn = true;

    @CreatedDate
    @Column(name = "join_dt", nullable = false)
    private LocalDateTime joinDt;

    @LastModifiedDate
    @Column(name = "update_dt")
    private LocalDateTime lastUpdateDt;

    @Column(name = "last_auth_dt")
    private LocalDateTime lastAuthDt;

    private BaseUserEntity(RoleTypeEnum roleType, String name, String email, String password) { this.roleType = roleType; this.name = name; this.email = email; this.password = password; this.loginCnt = 0; }

    public void linkProvider(ProviderEnum provider, String providerId) { this.provider = provider; this.providerId = providerId; }
}
