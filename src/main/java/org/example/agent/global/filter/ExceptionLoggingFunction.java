package org.example.agent.global.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.agent.global.constrant.LogMarker;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExceptionLoggingFunction {

    private static final ObjectMapper objectMapper = new ObjectMapper();;
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    }

    public void exceptionLog(Exception e) {
        try {
            e.printStackTrace(System.out);
            log.error(LogMarker.SERVICE.getMarker(), "Error : {}", objectMapper.writeValueAsString(e.getStackTrace()));
        } catch (Exception _e) {
            e.printStackTrace();
        }
    }

}
