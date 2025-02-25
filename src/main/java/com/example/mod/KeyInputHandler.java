package com.example.mod;

import com.example.mod.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyInputHandler {

    public static KeyBinding openGuiKey;

    public KeyInputHandler() {
        openGuiKey = new KeyBinding("key.examplemod.open_gui", GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.examplemod");
        ClientRegistry.registerKeyBinding(openGuiKey);
    }

    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!ExampleMod.active) {
            return;
        }
        if (openGuiKey.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new UI(ExampleMod.moduleManager));
        }
    }
}
