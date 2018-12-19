package com.geektime.systrace;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectUtil {
    public static Field getDeclaredFieldRecursive(Object clazz, String fieldName) throws NoSuchFieldException, ClassNotFoundException {
        Class<?> realClazz = null;
        if (clazz instanceof String) {
            realClazz = Class.forName((String) clazz);
        } else if (clazz instanceof Class) {
            realClazz = (Class<?>) clazz;
        } else {
            throw new IllegalArgumentException("Illegal clazz type: " + clazz.getClass());
        }
        Class<?> currClazz = realClazz;
        while (true) {
            try {
                Field field = currClazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                if (currClazz.equals(Object.class)) {
                    throw e;
                }
                currClazz = currClazz.getSuperclass();
            }
        }
    }

    public static Method getDeclaredMethodRecursive(Object clazz, String methodName, Class<?>... argTypes) throws NoSuchMethodException, ClassNotFoundException {
        Class<?> realClazz = null;
        if (clazz instanceof String) {
            realClazz = Class.forName((String) clazz);
        } else if (clazz instanceof Class) {
            realClazz = (Class<?>) clazz;
        } else {
            throw new IllegalArgumentException("Illegal clazz type: " + clazz.getClass());
        }
        Class<?> currClazz = realClazz;
        while (true) {
            try {
                Method method = currClazz.getDeclaredMethod(methodName);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                if (currClazz.equals(Object.class)) {
                    throw e;
                }
                currClazz = currClazz.getSuperclass();
            }
        }
    }

    private ReflectUtil() {
        throw new UnsupportedOperationException();
    }
}
