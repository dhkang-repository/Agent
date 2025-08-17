package org.example.agent.global.util;

import org.example.agent.global.constrant.ErrorCode;
import org.example.agent.global.exception.DefineException;

public class SecretKeyValidUtil {

    public static boolean isUse(String secretKey) {
        return secretKey.equals("test");
    }

    public static void valid(String secretKey) {
        if(!isUse(secretKey)){
            throw new DefineException(ErrorCode.SECRET_KEY_NOT_VALID, "시크릿 키가 유효하지 않습니다.");
        }
    }
}
