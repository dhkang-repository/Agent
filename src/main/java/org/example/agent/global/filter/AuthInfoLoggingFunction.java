package org.example.agent.global.filter;

import org.example.agent.global.security.SecurityAuthUser;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

public interface AuthInfoLoggingFunction {

    static void logAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if(principal instanceof SecurityAuthUser authUser) {
            MDC.put("principal", authUser.getEmail());
        }
    }

}
