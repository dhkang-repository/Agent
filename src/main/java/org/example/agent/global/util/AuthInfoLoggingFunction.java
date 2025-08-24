package org.example.agent.global.util;

import org.example.agent.global.security.SecurityAuthUser;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

public interface AuthInfoLoggingFunction {

    static Long logAuthenticationAndGetUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if(principal instanceof SecurityAuthUser authUser) {
            MDC.put("principal", authUser.getEmail());
            return authUser.getUserId();
        }

        return null;
    }

}
