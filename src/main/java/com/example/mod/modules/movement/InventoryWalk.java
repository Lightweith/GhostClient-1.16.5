package com.example.mod.modules.movement;

import com.example.mod.hooks.ForgeEventHook;
import com.example.mod.modules.Category;
import com.example.mod.hooks.MovementInputFromOptionsHook;
import com.example.mod.modules.obf.ObfuscatedModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.event.TickEvent;

public class InventoryWalk extends ObfuscatedModule {

    private final Minecraft mc = Minecraft.getInstance();

    public InventoryWalk() {
        super(new String[]{"In", "v","ent", "or","y", "W", "al", "k"}, Category.BEG);
    }

    @Override
    public void onEnable() {
        assert mc.player != null;
        if (!(mc.player.movementInput instanceof MovementInputFromOptionsHook)) {
            mc.player.movementInput = new MovementInputFromOptionsHook(mc.gameSettings);
        }
        ForgeEventHook.register(this);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.movementInput = new MovementInputFromOptions(mc.gameSettings);
        }
        ForgeEventHook.unregister(this);
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player != null && mc.player.movementInput instanceof MovementInputFromOptionsHook) {
        }
    }
}
