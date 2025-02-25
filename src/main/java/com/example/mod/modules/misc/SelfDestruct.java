package com.example.mod.modules.misc;

import com.example.mod.hooks.ForgeEventHook;
import com.example.mod.ExampleMod;
import com.example.mod.modules.Category;
import com.example.mod.modules.Module;
import com.example.mod.modules.obf.ObfuscatedModule;
import net.minecraft.client.Minecraft;

public class SelfDestruct extends ObfuscatedModule {

    public SelfDestruct() {
        super(new String[] { "SelfD", "estr", "uct" }, Category.OST);
    }

    @Override
    public void onEnable() {
        System.gc();

        if (Minecraft.getInstance().currentScreen != null) {
            Minecraft.getInstance().displayGuiScreen(null);
        }

        for (Module module : ExampleMod.moduleManager.getModules()) {
            if (module.isToggled()) {
                module.toggle();
            }
        }

        ForgeEventHook.unregister(this);
        ExampleMod.active = false;

        System.gc();
    }

    @Override
    public void onDisable() {

    }
}
