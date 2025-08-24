package org.example.agent.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public interface UrlMethodLoggingFunction {

    static void urlMethodLogging(HttpServletRequest servletRequest) {

        String requestURI = servletRequest.getRequestURI();
        String method = servletRequest.getMethod();

        MDC.put("uri", requestURI);
        MDC.put("method", method);

    }

}
