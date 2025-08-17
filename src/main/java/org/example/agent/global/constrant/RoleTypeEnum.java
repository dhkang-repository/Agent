package org.example.agent.global.constrant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.example.agent.global.exception.DefineException;
import org.example.agent.global.util.EnumCommon;

@Getter
public enum RoleTypeEnum implements EnumCommon<RoleTypeEnum> {
    SS("SUPER ADMIN"),
    S1("ADMIN"),
    L1("LEVEL 1 USER"),
    L2("LEVEL 2 USER"),
    UN("UNKNOWN");


    private final String desc;

    RoleTypeEnum(String desc) {
        this.desc = desc;
    }


    @Override
    public RoleTypeEnum getDefault() {
        return UN;
    }

    public static RoleTypeEnum get(String _type) {
        return EnumCommon.valueOfOrDefault(RoleTypeEnum.class, _type);
    }

    @JsonCreator
    public static RoleTypeEnum parsing(String _type) {
        RoleTypeEnum type = get(_type);

        if (type == UN) {
            throw new DefineException(ErrorCode.PARAMETER_TYPE_NOT_VALID, "role type not valid");
        }

        return type;
    }
}
