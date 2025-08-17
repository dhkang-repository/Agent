package org.example.agent.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.constrant.LogMarker;
import org.example.agent.global.dto.ResponseHeader;
import org.example.agent.global.dto.ResponseResult;
import org.example.agent.global.util.HttpRequestEndPointChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint, CustomCorsHeader {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HttpRequestEndPointChecker endpointChecker;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String method = request.getMethod();
        StringBuffer requestURL = request.getRequestURL();
        Object originURL = request.getAttribute("url");
        log.info(LogMarker.SERVICE.getMarker(), "SECURITY ERROR || METHOD: {} || URL : {} || ORIGIN URL: {}", method, requestURL, originURL);

        String origin = request.getHeader("Origin"); // 요청한 Origin 가져오기

        addHeaders(origin, response);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        response.setContentType("application/json");

        PrintWriter writer = response.getWriter();
        String responseMessage;
        if (!endpointChecker.isEndpointExist(request)) {
            ErrorCode resourceNotFound = ErrorCode.RESOURCE_NOT_FOUND;
            ResponseResult responseResult = ResponseResult.of(
                    ResponseHeader.of(false, resourceNotFound.getCode(), resourceNotFound.getMessage()),
                    null);

            responseMessage = objectMapper.writeValueAsString(responseResult);
        } else {
            ErrorCode errorCode;

            Object exceptionRequest = request.getAttribute("exception");
            if (exceptionRequest != null) {
                errorCode = (ErrorCode) request.getAttribute("exception");
            } else {
                errorCode = ErrorCode.ACCESS_TOKEN_NOT_VALID;
            }

            ResponseResult responseResult = ResponseResult.of(
                    ResponseHeader.of(false, errorCode.getCode(), errorCode.getMessage()),
                    null);

            responseMessage = objectMapper.writeValueAsString(responseResult);
        }

        writer.write(responseMessage);
        writer.flush();
        writer.close();
    }
}
