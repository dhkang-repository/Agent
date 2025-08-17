package org.example.agent.entity.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@ToString(callSuper = false)
@DynamicUpdate
@Table(name = "tbl_auth_role", schema = "agent_db", catalog = "agent_db")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityEntity {
    @Comment("권한 식별자")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Comment("권한 명칭")
    @Column(name = "role_name")
    private String name;

    @Comment("권한 타입 ")
    @Column(name = "role_type")
    private String type;

    @Comment("권한 코멘트 및 설명")
    @Column(name = "role_desc")
    private String description;

}
