package org.example.agent.entity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
@Table(
        name = "tbl_auth_user",
        schema = "agent_db",
        catalog = "agent_db",
        uniqueConstraints = @UniqueConstraint(name="uk_auth_provider", columnNames = {"provider_type", "provider_id"})
)
public class AuthUserEntity extends BaseUserEntity {
}
