//package com.example.mod.utils.oldtest;
//
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.lang.reflect.Field;
//import java.util.Arrays;
//public class UnsafeString {
//    private static final MethodHandle GET_STRING_VALUE, SET_STRING_VALUE, SET_STRING_HASHCODE;
//
//    static {
//        try {
//            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//            field.setAccessible(true);
//            MethodHandles.Lookup lookup = (MethodHandles.Lookup) field.get(null);
//            GET_STRING_VALUE = lookup.findGetter(String.class, "value", char[].class);
//            SET_STRING_VALUE = lookup.findSetter(String.class, "value", char[].class);
//            SET_STRING_HASHCODE = lookup.findSetter(String.class, "hash", int.class);
//        } catch (Throwable t) {
//            throw new ExceptionInInitializerError(t);
//        }
//    }
//
//    private final String string;
//
//    public UnsafeString(String value) {
//        this.string = value;
//    }
//
//    public void clear() {
//        try {
//            Arrays.fill((char[]) GET_STRING_VALUE.invokeExact(string), (char) 0);
//            SET_STRING_VALUE.invokeExact(string, new char[0]);
//            SET_STRING_HASHCODE.invokeExact(string, 0);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public String toString() {
//        return string;
//    }
//}