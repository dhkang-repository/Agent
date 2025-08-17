package org.example.agent.global.exception;

import lombok.Getter;
import org.example.agent.global.constrant.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    @Getter
    private final ErrorCode errorCode;

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage(), null);
        this.errorCode = errorCode;
    }

    public JwtAuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public static class JwtTokenNotValid extends JwtAuthenticationException {
        public JwtTokenNotValid() {
            super(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }

        public JwtTokenNotValid(Throwable cause) {
            super(ErrorCode.ACCESS_TOKEN_NOT_VALID, cause);
        }
    }

    public static class JwtExpiredValid extends JwtAuthenticationException {
        public JwtExpiredValid(Throwable cause) {
            super(ErrorCode.ACCESS_TOKEN_EXPIRED, cause);
        }
    }


}
