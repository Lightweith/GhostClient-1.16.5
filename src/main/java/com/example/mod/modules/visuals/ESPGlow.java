package com.example.mod.modules.visuals;

import com.example.mod.hooks.ForgeEventHook;
import com.example.mod.modules.Category;
import com.example.mod.modules.obf.ObfuscatedModule;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class ESPGlow extends ObfuscatedModule {

    private final Minecraft mc = Minecraft.getInstance();

    public ESPGlow() {
        super(new String[]{"Glow"}, Category.BAY);
    }

    @Override
    public void onEnable() {
        ForgeEventHook.register(this);
    }

    @Override
    public void onDisable() {
        ForgeEventHook.unregister(this);
        if (mc.world != null) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player != mc.player) {
                    player.setGlowing(false);
                }
            }
        }
    }

    public void onRender(RenderPlayerEvent event) {
        if (isToggled() && event.getEntity() != mc.player) {
            event.getEntity().setGlowing(true);
        }
    }

    static {
        ForgeEventHook.register(ESPGlow.class);
    }
}
