package org.example.agent.global.constrant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.util.EnumCommon;

@Getter
public enum ProviderEnum implements EnumCommon<ProviderEnum> {
    GL("GOOGLE"),
    NV("NAVER"),
    IN("INTERNAL"),
    UN("UNKNOWN")
    ;

    private final String desc;

    ProviderEnum(String desc) {
        this.desc = desc;
    }


    @Override
    public ProviderEnum getDefault() {
        return UN;
    }

    public static ProviderEnum get(String _type) {
        return EnumCommon.valueOfOrDefault(ProviderEnum.class, _type);
    }

    @JsonCreator
    public static ProviderEnum parsing(String _type) {
        ProviderEnum type = get(_type);

        if (type == UN) {
            throw new DefineException(ErrorCode.PARAMETER_TYPE_NOT_VALID, "provider type not valid");
        }

        return type;
    }
}
