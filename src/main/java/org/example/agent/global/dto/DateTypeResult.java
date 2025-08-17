package org.example.agent.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.agent.global.constrant.DateTypeEnum;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
public class DateTypeResult <T> {
    private DateTypeEnum type;
    private String startDate;
    private String endDate;
    private List<T> elements;

    public DateTypeResult(DateTypeEnum type, String startDate, String endDate, List<T> elements) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.elements = elements;
    }
}
