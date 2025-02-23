package com.example.mod.hooks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRendererHook {

    public interface IRenderFirstPerson {
        boolean onRenderHand(RenderHandEvent event);
        int getPriority();
    }

    private static final List<IRenderFirstPerson> renderCallbacks = new CopyOnWriteArrayList<>();

    public static void registerRenderCallback(IRenderFirstPerson callback) {
        if (!renderCallbacks.contains(callback)) {
            renderCallbacks.add(callback);
            renderCallbacks.sort(Comparator.comparingInt(IRenderFirstPerson::getPriority));
        }
    }

    public static void unregisterRenderCallback(IRenderFirstPerson callback) {
        renderCallbacks.remove(callback);
    }

    public static void clearRenderCallbacks() {
        renderCallbacks.clear();
    }

    public static void onRenderHand(RenderHandEvent event) {
        for (IRenderFirstPerson callback : renderCallbacks) {
            if (callback.onRenderHand(event)) {
                event.setCanceled(true);
                return;
            }
            if (event.isCanceled()) break;
        }
    }

    static {
        ForgeEventHook.register(FirstPersonRendererHook.class);
    }
}
