package com.example.mod.utils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectVoid {
    private final Object Instance;
    private final Class aClass;
    private Method method = null;

    public ReflectVoid(Object Instance, Class clas, int num) {
        this.Instance = Instance;
        this.aClass = clas;
        method = aClass.getDeclaredMethods()[num];
    }

    public ReflectVoid(Object Instance, Class clas, Class class2) {
        this.Instance = Instance;
        this.aClass = clas;
        for (Method tracker : aClass.getDeclaredMethods()) {
            if (tracker.getReturnType().equals(class2)) {
                method = tracker;
                return;
            }
        }
    }

    public ReflectVoid(Object Instance, Class clas, String field) {

        this.Instance = Instance;
        this.aClass = clas;
        try {

            method = aClass.getDeclaredMethod(field);
        } catch (Exception ignored) {
            System.out.println("Error Void " + field);
        }

    }

    public void sout() {
        try {
            int a = 0;
            for (Method tracker : aClass.getDeclaredMethods()) {
                System.out.println(a + " " + tracker.getName());
                a++;
            }
        } catch (Exception ignored) {
        }
    }

    public void invoke(Object... objects) {
        try {
            method.setAccessible(true);
            method.invoke(Instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Error Void " + e);
        }
    }
}