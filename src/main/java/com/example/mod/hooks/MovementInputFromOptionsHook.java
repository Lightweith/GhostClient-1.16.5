package com.example.mod.hooks;

import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.MovementInputFromOptions;

public class MovementInputFromOptionsHook extends MovementInputFromOptions {

    private final GameSettings options;
    private final Minecraft mc = Minecraft.getInstance();

    public MovementInputFromOptionsHook(GameSettings options) {
        super(options);
        this.options = options;
    }

    @Override
    public void tickMovement(boolean reducedSpeed) {
        this.forwardKeyDown = isKeyDown(options.keyBindForward.getKey().getKeyCode());
        this.backKeyDown = isKeyDown(options.keyBindBack.getKey().getKeyCode());
        this.leftKeyDown = isKeyDown(options.keyBindLeft.getKey().getKeyCode());
        this.rightKeyDown = isKeyDown(options.keyBindRight.getKey().getKeyCode());
        this.moveForward = (forwardKeyDown == backKeyDown) ? 0.0F : (forwardKeyDown ? 1.0F : -1.0F);
        this.moveStrafe = (leftKeyDown == rightKeyDown) ? 0.0F : (leftKeyDown ? 1.0F : -1.0F);
        this.jump = isKeyDown(options.keyBindJump.getKey().getKeyCode());
        this.sneaking = options.keyBindSneak.isKeyDown();

        if (reducedSpeed) {
            this.moveForward *= 0.3F;
            this.moveStrafe *= 0.3F;
        }
    }

    private boolean isKeyDown(int keyCode) {
        MainWindow window = mc.getMainWindow();
        return !(mc.currentScreen instanceof ChatScreen) && InputMappings.isKeyDown(window.getHandle(), keyCode);
    }
}
