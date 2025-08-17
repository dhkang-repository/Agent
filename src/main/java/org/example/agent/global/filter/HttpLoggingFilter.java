package org.example.agent.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.agent.global.constrant.LogMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class HttpLoggingFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        UuidLoggingFunction.uuidLogging();
        UrlMethodLoggingFunction.urlMethodLogging(httpServletRequest);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpServletResponse);

        long start = System.currentTimeMillis();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long end = System.currentTimeMillis();

        String requestBody = new String(wrappedRequest.getContentAsByteArray()).replaceAll("(\r\n|\r|\n|\n\r)|( )", "");

        // 쿠키 로깅
        Cookie[] cookies = httpServletRequest.getCookies();
        StringBuilder cookieBuilder = new StringBuilder();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieBuilder.append(
                        String.format( "(%s = %s)",
                            cookie.getName(), cookie.getValue())
                );
            }
        }

        // Authorization 헤더 확인
        String headerNames  = String.join(", ", Collections.list(httpServletRequest.getHeaderNames()));
        String requestAccessRequestHeaders = httpServletRequest.getHeader("Access-Control-Request-Headers");

        // log the request
        LOGGER.info(LogMarker.SERVICE.getMarker(),
                """
                TIME : {} || QUERYSTRING : {} || Body : {} || ORIGIN : {} || Access Request Headers : {} || IP : {}  || HEADER NAMES : {}""",

                end - start, // time

                nonNull(wrappedRequest.getQueryString()) ? wrappedRequest.getQueryString() : "", // QUERY STRING

                requestBody, // BODY

                nonNull(httpServletRequest.getHeader("Origin")) ?
                        httpServletRequest.getHeader("Origin") :
                        "-",
                nonNull(requestAccessRequestHeaders) ?
                        requestAccessRequestHeaders :
                        "-",

                (null != wrappedRequest.getHeader("X-FORWARDED-FOR")) ? // IP
                        wrappedRequest.getHeader("X-FORWARDED-FOR") :
                        wrappedRequest.getRemoteAddr(),


                headerNames
        );

        MDC.clear();
        wrappedResponse.copyBodyToResponse();
    }
}
