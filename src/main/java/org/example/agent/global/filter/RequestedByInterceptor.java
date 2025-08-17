package org.example.agent.global.filter;

import org.example.agent.global.security.authentication.AuthenticationHolder;
import org.example.agent.global.security.authentication.RequestedBy;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Component
public class RequestedByInterceptor implements WebRequestInterceptor {
    public static final String REQUESTED_BY_HEADER = "Secret";

    private final AuthenticationHolder authenticationHolder;

    public RequestedByInterceptor(AuthenticationHolder authenticationHolder) {
        this.authenticationHolder = authenticationHolder;
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {

    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }

    @Override
    public void preHandle(WebRequest request) throws Exception {
        String requestedBy = request.getHeader(REQUESTED_BY_HEADER);

        RequestedBy requestUser = new RequestedBy(requestedBy);

        authenticationHolder.setAuthentication(requestUser);


    }
}
