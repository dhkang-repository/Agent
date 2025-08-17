package org.example.agent.global.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.constrant.LogMarker;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

@Slf4j
@Component
public class CorsLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 전 처리
        filterChain.doFilter(request, response);

        // 요청 후 응답 헤더 확인 및 로깅
        Enumeration<String> requestHeaderNames = request.getHeaderNames();
        log.info(LogMarker.SERVICE.getMarker(), "[CORS Request Logging] Request URI: {}, Origin: {}, Referer: {}",
                request.getRequestURI(), request.getHeader("Origin"), request.getHeader("Referer"));

        for (Iterator<String> it = requestHeaderNames.asIterator(); it.hasNext(); ) {
            String header = it.next();
            if(header.toLowerCase().startsWith("access-control")) {
                log.info(LogMarker.SERVICE.getMarker(), "    {} : {}", header, request.getHeader(header));
            }
        }

        Collection<String> headerNames = response.getHeaderNames();
        log.info(LogMarker.SERVICE.getMarker(), "[CORS Response Logging] Request URI: {}, Origin: {}, Referer: {}",
                request.getRequestURI(), request.getHeader("Origin"), request.getHeader("Referer"));
        headerNames.stream()
                .filter(header -> header.toLowerCase().startsWith("access-control"))
                .forEach(header -> {
                    log.info(LogMarker.SERVICE.getMarker(), "    {} : {}", header, response.getHeader(header));
                });
    }
}
