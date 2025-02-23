package com.example.mod;

import com.example.mod.hooks.ForgeEventHook;
import com.example.mod.modules.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.util.Timer;

@Mod("examplemod")
public class ExampleMod {

    private static ExampleMod instance;
    public static ModuleManager moduleManager;
    public Timer timer;
    public static boolean active = true;
    public static final Minecraft mc = Minecraft.getInstance();

    public ExampleMod() {
        instance = this;
        moduleManager = new ModuleManager();
        active = true;

        MinecraftForge.EVENT_BUS.register(moduleManager);
        MinecraftForge.EVENT_BUS.register(moduleManager.getModules().toArray());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        KeyInputHandler keyHandler = new KeyInputHandler();
        ForgeEventHook.register(keyHandler);
    }

    public static ExampleMod getInstance() {
        return instance;
    }
}
