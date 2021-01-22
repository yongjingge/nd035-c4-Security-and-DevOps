package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects (Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            boolean accessibility = field.canAccess(target);
            // check whether the field is private or not
            if (! accessibility) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, toInject);
            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
