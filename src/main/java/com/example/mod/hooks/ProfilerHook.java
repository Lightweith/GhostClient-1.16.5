package com.example.mod.hooks;

import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProfilerHook {

    private static IProfiler profiler;

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                profiler = server.getProfiler();
            }
        }
    }

    public static void startSection(String sectionName) {
        if (profiler != null) {
            profiler.startSection(sectionName);
        }
    }

    public static void endSection() {
        if (profiler != null) {
            profiler.endSection();
        }
    }

    public static void markSection(String sectionName) {
        if (profiler != null) {
            profiler.func_230035_c_(sectionName);
        }
    }

    static {
        ForgeEventHook.register(ProfilerHook.class);
    }
}
