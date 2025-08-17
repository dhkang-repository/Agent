package org.example.agent.global.security.authentication;

import java.util.Optional;

public interface RequestedByProvider {
    Optional<String> getRequestedBy();
}
