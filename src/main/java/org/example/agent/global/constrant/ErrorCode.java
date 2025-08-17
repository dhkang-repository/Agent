package org.example.agent.global.constrant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    BYPASS_ERROR("GN0000", "에러가 발생했습니다."),

    // ## HTTP
    PARAMETER_ERROR("GN0002", "파라미터 에러"),
    PARAMETER_BODY_ERROR("GN0003", "파라미터 Body 에러"),
    PARAMETER_TYPE_NOT_VALID("GN0004", "Type이 유효하지 않습니다."),
    PARAMETER_DATE_TIME_FORMAT_VALID("GN0005", "Datetime format is not valid"),

    // ## AUTH - 1000
    ACCESS_NOT_VALID("GN1001", "권한이 유효하지 않습니다."),
    RESOURCE_NOT_FOUND("GN1002", "URI가 존재하지 않습니다."),

    ACCESS_TOKEN_EXPIRED("GN1003", "Access Token이 만료 되었습니다."),
    REFRESH_TOKEN_EXPIRED("GN1004", "Refresh Token이 만료 되었습니다."),

    ACCESS_TOKEN_NOT_VALID("GN1005", "Access Token이 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_VALID("GN1006", "Refresh Token이 유효하지 않습니다."),

    PASSWORD_NOT_VALID("GN1007", "비밀번호가 일치하지 않습니다."),
    SECRET_KEY_NOT_VALID("GN1008", "시크릿 키가 유효하지 않습니다."),

    // ## ENTITY - 2000
    ENTITY_NOT_EXIST("LN2001", "Entity 미존재"),
    ENTITY_EXIST("LN2002", "Entity 존재"),

    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + message;
    }
}
