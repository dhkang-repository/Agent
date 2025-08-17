package org.example.agent.global.security.authentication;

import org.example.agent.global.filter.RequestedByInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@EnableWebMvc
public class RequestedByMvcConfigurer implements WebMvcConfigurer {
    private final RequestedByInterceptor requestedByInterceptor;

    public RequestedByMvcConfigurer(RequestedByInterceptor requestedByInterceptor) {
        this.requestedByInterceptor = requestedByInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(requestedByInterceptor);
    }
}
