package org.example.agent.global.filter;

import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.constrant.GlobalConst;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.exception.JwtAuthenticationException;
import org.example.agent.global.util.ExceptionLoggingFunction;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionLoggingFunction exceptionLoggingFunction;

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<?> noHandlerFoundException(Exception ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, errorCode.getCode(), errorCode.getMessage()), null));
    }

    @ExceptionHandler({MissingServletRequestPartException.class})
    public ResponseEntity<?> missingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, "400", "data is required"), ex.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        String message = ex.getBindingResult().getFieldErrors().stream().map(error -> String.format(GlobalConst.ARGUMENT_MESSAGE_FORMAT, error.getField(), error.getDefaultMessage())).collect(Collectors.joining(" | "));

        ErrorCode errorCode = ErrorCode.PARAMETER_ERROR;

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(errorCode.getCode()), errorCode.getMessage()), message));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<?> missingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        String message = String.format(GlobalConst.ARGUMENT_MESSAGE_FORMAT, ex.getParameterName(), "필수입니다.");

        ErrorCode errorCode = ErrorCode.PARAMETER_ERROR;

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(errorCode.getCode()), errorCode.getMessage()), message));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        ErrorCode errorCode = ErrorCode.PARAMETER_BODY_ERROR;

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(errorCode.getCode()), errorCode.getMessage()), null));
    }

    @ExceptionHandler({EntityExistsException.class})
    public ResponseEntity<?> entityExistsException(EntityExistsException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(HttpStatus.BAD_REQUEST.value()), "Already Exist Entity"), null));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> dataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Data Integrity Error"), null));
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<?> noResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, String.valueOf(HttpStatus.NOT_FOUND.value()), "RESOURCE NOT FOUND"), null));
    }

    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<?> dateTimeParseException(DateTimeParseException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        ErrorCode errorCode = ErrorCode.PARAMETER_DATE_TIME_FORMAT_VALID;

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, errorCode.getCode(), errorCode.getMessage()), null));
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ResponseEntity<?> authorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        ErrorCode errorCode = ErrorCode.ACCESS_NOT_VALID;

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, errorCode.getCode(), errorCode.getMessage()), null));
    }

    // 비즈니스 프로세스 상의 이슈는 모두 상태 200으로 전달한다.
    @ExceptionHandler({DefineException.class, JwtAuthenticationException.class})
    public ResponseEntity<?> defineException(DefineException ex, HttpServletRequest request) {
        exceptionLoggingFunction.exceptionLog(ex);

        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();

        return ResponseEntity.status(errorCode.getHttpStatus()).contentType(MediaType.APPLICATION_JSON)
                .body(ResponseResult.of(ResponseHeader.of(false, errorCode.getCode(), errorCode.getMessage()), message));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity otherException(Exception ex,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        exceptionLoggingFunction.exceptionLog(ex);

        // Content-Type 강제 세팅
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        ResponseResult responseResult = ResponseResult.of(
                ResponseHeader.of(false, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "Internal Server Error"),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseResult);
    }

}