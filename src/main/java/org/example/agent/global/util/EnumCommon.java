package org.example.agent.global.util;

public interface EnumCommon<E extends Enum<E>> {
    /**
     * Enum 공통 getter 기능
     */
    E getDefault();

    static <E extends Enum<E> & EnumCommon<E>> E valueOfOrDefault(Class<E> enumClass, String name) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return enumClass.getEnumConstants()[0].getDefault(); // fallback
    }
}
