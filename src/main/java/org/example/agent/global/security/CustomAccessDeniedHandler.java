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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler, CustomCorsHeader {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String method = request.getMethod();
        StringBuffer requestURL = request.getRequestURL();
        Object originURL = request.getAttribute("url");
        log.info(LogMarker.SERVICE.getMarker(), "SECURITY ERROR || METHOD: {} || URL : {} || ORIGIN URL: {}", method, requestURL, originURL);

        String origin = request.getHeader("Origin"); // 요청한 Origin 가져오기

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        addHeaders(origin, response);

        PrintWriter writer = response.getWriter();

        ErrorCode accessNotValid = ErrorCode.ACCESS_NOT_VALID;

        ResponseResult responseResult = ResponseResult.of(
                ResponseHeader.of(false, accessNotValid.getCode(), accessNotValid.getMessage()),
                null);

        String responseMessage = objectMapper.writeValueAsString(responseResult);

        writer.write(responseMessage);
        writer.flush();
        writer.close();
    }
}
