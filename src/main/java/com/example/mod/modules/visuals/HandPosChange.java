package com.example.mod.modules.visuals;

import com.example.mod.modules.Category;
import com.example.mod.modules.obf.ObfuscatedModule;
import com.example.mod.settings.obf.ObfuscatedFloatSetting;
import com.example.mod.hooks.FirstPersonRendererHook;
import net.minecraftforge.client.event.RenderHandEvent;

public class HandPosChange extends ObfuscatedModule {
    private final ObfuscatedFloatSetting xOffset = new ObfuscatedFloatSetting(new String[]{"XOff", "s", "e", "t"}, 0.0f, -1.0f, 1.0f);
    private final ObfuscatedFloatSetting yOffset = new ObfuscatedFloatSetting(new String[]{"YOff", "s", "e", "t"}, 0.0f, -1.0f, 1.0f);
    private final ObfuscatedFloatSetting zOffset = new ObfuscatedFloatSetting(new String[]{"ZOff", "s", "e", "t"}, 0.0f, -1.0f, 1.0f);

    private FirstPersonRendererHook.IRenderFirstPerson renderCallback;

    public HandPosChange() {
        super(new String[]{"Ha", "nd", "P", "os", "ition", "Cha", "nge"}, Category.BAY);
        addSetting(xOffset);
        addSetting(yOffset);
        addSetting(zOffset);
    }

    @Override
    public void onEnable() {
        renderCallback = new FirstPersonRendererHook.IRenderFirstPerson() {
            @Override
            public boolean onRenderHand(RenderHandEvent event) {
                event.getMatrixStack().translate(xOffset.getValue(), yOffset.getValue(), zOffset.getValue());
                return false;
            }

            @Override
            public int getPriority() {
                return 0;
            }
        };
        FirstPersonRendererHook.registerRenderCallback(renderCallback);
    }

    @Override
    public void onDisable() {
        if (renderCallback != null) {
            FirstPersonRendererHook.unregisterRenderCallback(renderCallback);
            renderCallback = null;
        }
    }
}
