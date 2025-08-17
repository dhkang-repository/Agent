package org.example.agent.global.constrant;

import java.time.format.DateTimeFormatter;

public class GlobalConst {
    public static final DateTimeFormatter GENERAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String ARGUMENT_MESSAGE_FORMAT = "%s -> %s";
    public static final String BASE_URL = "/agent/v1.0";
}
