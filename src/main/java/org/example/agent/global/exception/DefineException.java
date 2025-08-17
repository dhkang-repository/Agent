package org.example.agent.global.exception;

import lombok.Getter;
import org.example.agent.global.constrant.ErrorCode;

public class DefineException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    @Getter
    private String message;

    public DefineException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public DefineException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = message;
    }
}
