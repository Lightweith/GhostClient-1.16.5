package com.example.mod.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectFileld {
    private final Object Instance;
    private final Class aClass;
    private Field localField = null;

    public ReflectFileld(Object Instance, Class clas, int num) {
        this.Instance = Instance;
        this.aClass = clas;
        localField = aClass.getDeclaredFields()[num];
    }

    public ReflectFileld(Object Instance, Class clas, Class class2) {
        this.Instance = Instance;
        this.aClass = clas;
        for (Field tracker : aClass.getDeclaredFields()) {
            if (tracker.getType().equals(class2)) {
                localField = tracker;
            }
        }
    }

    public ReflectFileld(Object Instance, Class clas, String field) {
        this.Instance = Instance;
        this.aClass = clas;
        try {
            localField = aClass.getDeclaredField(field);
        } catch (Exception ignored) {
            System.out.println("Error field " + field);
        }

    }

    public Object getValue() {

        try {
            localField.setAccessible(true);
            return localField.get(Instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
    public Object getFinalValue() {

        try {
            Field modifier;
            localField.setAccessible(true);
            modifier = localField.getClass().getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.setInt(localField, localField.getModifiers() & ~Modifier.FINAL);
            return localField.get(Instance);
        } catch (Exception e) {
            return null;
        }
    }
    public void setValue(Object value) {
        try {
            localField.setAccessible(true);

            localField.set(Instance, value);
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }
    public void setValueFinal(Object value) {
        try {
            Field modifier;
            localField.setAccessible(true);
            modifier = localField.getClass().getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.setInt(localField, localField.getModifiers() & ~Modifier.FINAL);
            localField.set(Instance, value);
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }
    public void setValueProtected(Object value) {
        try {
            Field modifier;
            localField.setAccessible(true);
            modifier = localField.getClass().getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.setInt(localField, localField.getModifiers() & ~Modifier.PROTECTED);
            localField.set(Instance, value);
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }
    public void copyFrom(Object from) {
        try {
            localField.setAccessible(true);
            localField.set(Instance, localField.get(from));
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    public void copyFromFinal(Object o) {
        try {
            Field modifier;
            localField.setAccessible(true);
            modifier = localField.getClass().getDeclaredField("modifiers");
            modifier.setAccessible(true);
            modifier.setInt(localField, localField.getModifiers() & ~Modifier.FINAL);
            localField.set(Instance, localField.get(o));
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    public void copyFromAll(Object o) {
        try {
            for (Field f : aClass.getDeclaredFields()) {
                Field modifier;
                f.setAccessible(true);
                modifier = f.getClass().getDeclaredField("modifiers");
                modifier.setAccessible(true);
                modifier.setInt(f,Modifier.PUBLIC);
                f.set(Instance, f.get(o));
            }

        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }


}
