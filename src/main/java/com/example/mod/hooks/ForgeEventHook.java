package com.example.mod.hooks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ForgeEventHook {
    private static final Map<Object, List<Consumer<Event>>> listenersMap = new HashMap<>();

    public static void register(Object target) {
        if (target instanceof Class<?>) {
            Class<?> clazz = (Class<?>) target;
            for (Method m : clazz.getDeclaredMethods()) {
                if (!Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1)
                    continue;
                Class<?> paramType = m.getParameterTypes()[0];
                if (!Event.class.isAssignableFrom(paramType))
                    continue;
                m.setAccessible(true);
                Consumer<Event> consumer = event -> {
                    if (paramType.isInstance(event)) {
                        try {
                            m.invoke(null, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            System.err.println("Static handler error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                };
                MinecraftForge.EVENT_BUS.addListener(consumer);
            }
        } else {
            List<Consumer<Event>> consumers = new ArrayList<>();
            Class<?> clazz = target.getClass();
            for (Method m : clazz.getDeclaredMethods()) {
                if (Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1)
                    continue;
                Class<?> paramType = m.getParameterTypes()[0];
                if (!Event.class.isAssignableFrom(paramType))
                    continue;
                m.setAccessible(true);
                Consumer<Event> consumer = event -> {
                    if (paramType.isInstance(event)) {
                        try {
                            m.invoke(target, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            System.err.println("Instance handler error: " + e.getMessage());
                        }
                    }
                };
                MinecraftForge.EVENT_BUS.addListener(consumer);
                consumers.add(consumer);
            }
            if (!consumers.isEmpty()) {
                listenersMap.put(target, consumers);
            }
        }
    }

    public static void unregister(Object target) {
        List<Consumer<Event>> consumers = listenersMap.remove(target);
        if (consumers != null) {
            for (Consumer<Event> consumer : consumers) {
                MinecraftForge.EVENT_BUS.unregister(consumer);
            }
        }
    }
}
