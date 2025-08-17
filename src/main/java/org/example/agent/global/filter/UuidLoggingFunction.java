package org.example.agent.global.filter;

import org.slf4j.MDC;

import java.util.UUID;

public interface UuidLoggingFunction {
    static void  uuidLogging() {
        final UUID uuid = UUID.randomUUID();
        MDC.put("trx_id", uuid.toString());
    }
}
