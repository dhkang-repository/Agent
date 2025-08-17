package org.example.agent.entity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)                     // ✅ 동일하게 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
@Table(name = "tbl_auth_user_del", schema = "agent_db", catalog = "agent_db")
public class AuthUserDelEntity extends BaseUserEntity {
    public static AuthUserDelEntity from(AuthUserEntity s) {
        return AuthUserDelEntity.builder()
                 .id(s.getId())
                .email(s.getEmail())
                .name(s.getName())
                .password(s.getPassword())
                .roleType(s.getRoleType())
                .loginCnt(s.getLoginCnt())
                .provider(s.getProvider())
                .providerId(s.getProviderId())
                .nickname(s.getNickname())
                .phoneNumber(s.getPhoneNumber())
                .locale(s.getLocale())
                .timezone(s.getTimezone())
                .profileImage(s.getProfileImage())
                .marketingOptIn(s.isMarketingOptIn())
                .serviceOptIn(s.isServiceOptIn())
                .joinDt(s.getJoinDt())
                .lastUpdateDt(s.getLastUpdateDt())
                .lastAuthDt(s.getLastAuthDt())
                .build();
    }

}

