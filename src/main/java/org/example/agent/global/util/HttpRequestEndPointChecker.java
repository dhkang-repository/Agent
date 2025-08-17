package org.example.agent.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HttpRequestEndPointChecker {
    private final DispatcherServlet servlet;

    public boolean isEndpointExist(HttpServletRequest request) {
        for (HandlerMapping handlerMapping : Objects.requireNonNull(servlet.getHandlerMappings())) {
            try {
                HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
                if (foundHandler != null) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
