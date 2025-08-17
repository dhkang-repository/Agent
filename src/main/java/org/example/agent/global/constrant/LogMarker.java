package org.example.agent.global.constrant;

import lombok.Getter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Getter
public enum LogMarker {

    SERVICE("SERVICE"),
    ;

    private String type;
    private Marker marker;

    LogMarker(String type) {
        this.type = type;
        this.marker = MarkerFactory.getMarker(type);
    }


}
