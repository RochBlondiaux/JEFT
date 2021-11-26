package me.rochblondiaux.commons.utils;

import java.lang.reflect.ParameterizedType;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ReflectionUtils {

    public static Class<?> getGeneratedParameterType(Class<?> clazz) {
        return (Class<?>) ((ParameterizedType) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }
}
