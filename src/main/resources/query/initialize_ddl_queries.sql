DROP TABLE IF EXISTS `tbl_auth_role`;
CREATE TABLE IF NOT EXISTS `tbl_auth_role`
(
    `role_id`   BIGINT(19)                                                    NOT NULL AUTO_INCREMENT COMMENT '식별자',
    `role_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '권한 명칭',
    `role_type` CHAR(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NOT NULL DEFAULT '' COMMENT '권한 타입 : SUPER_ADMIN(S1) | 앱관리자(G1) | 보기(대시보드/앱관리)(G2))',
    `role_desc` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '권한 코멘트 및 설명',
    PRIMARY KEY (`role_id`) USING BTREE,
    UNIQUE KEY `uk_role_type` (`role_type`) USING BTREE,
    CONSTRAINT `ch1_role_type_auth_role` CHECK ((`role_type` in
                                                 ('S1', 'L1', 'L2')))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='권한 관리 테이블';

INSERT INTO `tbl_auth_role` (`role_id`, `role_name`, `role_type`, `role_desc`)
VALUES (1, '관리자', 'S1', '관리자');
INSERT INTO `tbl_auth_role` (`role_id`, `role_name`, `role_type`, `role_desc`)
VALUES (2, '사용자_L1', 'L1', '사용자 LEVEL 1');
INSERT INTO `tbl_auth_role` (`role_id`, `role_name`, `role_type`, `role_desc`)
VALUES (3, '사용자_L2', 'L2', '사용자 LEVEL 2');


DROP TABLE IF EXISTS `tbl_auth_token`;
CREATE TABLE IF NOT EXISTS `tbl_auth_token`
(
    `auth_user_id`  BIGINT(19)                                                NOT NULL COMMENT '유저 식별자',
    `access_token`  CHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '최근 발급 ACCESS TOKEN',
    `refresh_token` CHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '최근 발급 REFRESH TOKEN',
    `update_dt`     DATETIME(3) DEFAULT NULL COMMENT '최근 수정 일시 (UTC) (yyyy-MM-dd HH:mm:ss.SSS)',
    PRIMARY KEY (`auth_user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='사용자 보안 테이블';

DROP TABLE IF EXISTS `tbl_auth_user`;
CREATE TABLE IF NOT EXISTS `tbl_auth_user`
(
    `auth_user_id`  BIGINT(19)                                                    NOT NULL AUTO_INCREMENT COMMENT '식별자',
    `email`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '이메일',
    `user_name`     VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '이름',
    `user_password` CHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NOT NULL COMMENT '비밀번호',
    `role_type`     CHAR(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NOT NULL DEFAULT 'L2' COMMENT '부여된 권한 타입',
    `login_cnt`     INT UNSIGNED                                                  NOT NULL DEFAULT '0' COMMENT '로그인 횟수',
    `provider_type` VARCHAR(8)                                                    NOT NULL DEFAULT 'UN',
    `provider_id`   VARCHAR(128)                                                  NULL,

    `nickname`       VARCHAR(50)   NULL COMMENT '표시명',
    `phone_number`   VARCHAR(20)   NULL COMMENT '휴대폰',
    `locale`         VARCHAR(10)   NULL DEFAULT 'ko_KR' COMMENT '사용자 언어/로케일',
    `timezone`       VARCHAR(40)   NULL DEFAULT 'Asia/Seoul' COMMENT '표준 타임존 ID',
    `profile_image`  VARCHAR(512)  NULL COMMENT '프로필 이미지 URL',
    `mkt_opt_in`     TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '마케팅 수신 동의',
    `svc_opt_in`     TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '서비스 공지 수신 동의',

    `join_dt`       DATETIME(3)                                                   NOT NULL COMMENT '가입 일시 (yyyy-MM-dd HH:mm:ss)',
    `update_dt`     DATETIME(3)                                                            DEFAULT NULL COMMENT '최근 수정 일시 (yyyy-MM-dd HH:mm:ss)',
    `last_auth_dt`  DATETIME(3)                                                            DEFAULT NULL COMMENT '최근 로그인 일시 (yyyy-MM-dd HH:mm:ss)',
    PRIMARY KEY (`auth_user_id`) USING BTREE,
    UNIQUE KEY `uk_auth_email` (`email`) USING BTREE,
    UNIQUE KEY uk_auth_provider (provider_type, provider_id),
    CONSTRAINT `ch1_auth_type_auth_user`
        CHECK ((`role_type` in
                ('SS', 'S1', 'L1', 'L2')))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='사용자 관리 테이블';


DROP TABLE IF EXISTS `tbl_auth_user_del`;
CREATE TABLE IF NOT EXISTS `tbl_auth_user_del`
(
    `auth_user_id`  BIGINT(19)                                                    NOT NULL AUTO_INCREMENT COMMENT '식별자',
    `email`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '이메일',
    `user_name`     VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '이름',
    `phone_number`  VARCHAR(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '휴대폰 번호 (***-****-****)',
    `user_password` CHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NOT NULL COMMENT '비밀번호',
    `role_type`     CHAR(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci      NOT NULL DEFAULT 'L2' COMMENT '부여된 권한 타입',
    `join_dt`       DATETIME(3)                                                   NOT NULL COMMENT '가입 일시 (UTC) (yyyy-MM-dd HH:mm:ss)',
    `update_dt`     DATETIME(3)                                                            DEFAULT NULL COMMENT '최근 수정 일시 (UTC) (yyyy-MM-dd HH:mm:ss)',
    `last_auth_dt`  DATETIME(3)                                                            DEFAULT NULL COMMENT '최근 로그인 일시 (UTC) (yyyy-MM-dd HH:mm:ss)',
    PRIMARY KEY (`auth_user_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='삭제 사용자 관리 테이블';

