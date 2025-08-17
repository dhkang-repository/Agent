package org.example.agent.global.constrant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.util.EnumCommon;

@Getter
public enum DateTypeEnum implements EnumCommon<DateTypeEnum> {
    D("DATE_FORMAT(CONVERT_TZ({0}, 'UTC', 'Asia/Seoul'), '%Y-%m-%d')","%Y-%m-%d", "yyyy-MM-dd", "yyyy-MM-dd"),
    W("DATE_FORMAT(CONVERT_TZ({0}, 'UTC', 'Asia/Seoul'), '%Y-%v')", "%Y-%v","yyyy-MM-dd", "YYYY-ww"),
    M("DATE_FORMAT(CONVERT_TZ({0}, 'UTC', 'Asia/Seoul'), '%Y-%m')", "%Y-%m","yyyy-MM", "yyyy-MM"),
    UN(null, null, null,null)
    ;

    private String mysqlFormat;
    private String mybatisFormat;
    private String iterDateFormat;
    private String targetDateFormat;

    DateTypeEnum(String mysqlFormat, String mybatisFormat, String iterDateFormat, String targetDateFormat) {
        this.mysqlFormat = mysqlFormat;
        this.mybatisFormat = mybatisFormat;
        this.iterDateFormat = iterDateFormat;
        this.targetDateFormat = targetDateFormat;
    }

    @Override
    public DateTypeEnum getDefault() {
        return UN;
    }

    public static DateTypeEnum get(String _type) {
        return EnumCommon.valueOfOrDefault(DateTypeEnum.class, _type);
    }

    @JsonCreator
    public static DateTypeEnum parsing(String _type) {
        DateTypeEnum type = get(_type);

        if (type == UN) {
            throw new DefineException(ErrorCode.PARAMETER_TYPE_NOT_VALID, "date type not valid");
        }

        return type;
    }
}
