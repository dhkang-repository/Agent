package org.example.agent.global.constrant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    BYPASS_ERROR("GN0000", "에러가 발생했습니다.", HttpStatus.OK),

    // ## HTTP
    PARAMETER_ERROR("GN0002", "파라미터 에러", HttpStatus.OK),
    PARAMETER_BODY_ERROR("GN0003", "파라미터 Body 에러", HttpStatus.OK),
    PARAMETER_TYPE_NOT_VALID("GN0004", "Type이 유효하지 않습니다.", HttpStatus.OK),
    PARAMETER_DATE_TIME_FORMAT_VALID("GN0005", "Datetime format is not valid", HttpStatus.OK),

    // ## AUTH - 1000
    ACCESS_NOT_VALID("GN1001", "권한이 유효하지 않습니다.", HttpStatus.OK),
    RESOURCE_NOT_FOUND("GN1002", "URI가 존재하지 않습니다.", HttpStatus.OK),

    ACCESS_TOKEN_EXPIRED("GN1003", "Access Token이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("GN1004", "Refresh Token이 만료 되었습니다.", HttpStatus.UNAUTHORIZED),

    ACCESS_TOKEN_NOT_VALID("GN1005", "Access Token이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_VALID("GN1006", "Refresh Token이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),

    PASSWORD_NOT_VALID("GN1007", "비밀번호가 일치하지 않습니다.", HttpStatus.OK),
    SECRET_KEY_NOT_VALID("GN1008", "시크릿 키가 유효하지 않습니다.", HttpStatus.OK),

    // ## ENTITY - 2000
    ENTITY_NOT_EXIST("LN2001", "Entity 미존재", HttpStatus.OK),
    ENTITY_EXIST("LN2002", "Entity 존재", HttpStatus.OK),

    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + message;
    }
}
