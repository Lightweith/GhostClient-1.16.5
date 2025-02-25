package com.example.mod.modules.combat;

import com.example.mod.modules.Category;
import com.example.mod.modules.obf.ObfuscatedModule;
import com.example.mod.settings.obf.ObfuscatedBooleanSetting;
import com.example.mod.settings.obf.ObfuscatedFloatSetting;
import com.example.mod.hooks.ForgeEventHook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.TickEvent;

public class HitBox extends ObfuscatedModule {
    public final ObfuscatedFloatSetting hitboxExtra = new ObfuscatedFloatSetting(
            new String[]{"E", "xP", "a", "n", "d"}, 0.2f, 0f, 1.5f);

    public final ObfuscatedBooleanSetting invisibleRender = new ObfuscatedBooleanSetting(
            new String[]{"I", "nvi", "si", "b", "le"}, false);

    public static HitBox instance;

    public HitBox() {
        super(new String[]{"H", "itB", "o", "x"}, Category.KCH);
        addSetting(hitboxExtra);
        addSetting(invisibleRender);
        instance = this;
    }

    @Override
    public void onEnable() {
        ForgeEventHook.register(this);
    }

    @Override
    public void onDisable() {
        ForgeEventHook.unregister(this);
        resetAllPlayerHitboxes();
    }
    
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world == null) {
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            expandPlayerHitboxes(minecraft);
        } else if (event.phase == TickEvent.Phase.END && invisibleRender.getValue()) {
            resetAllPlayerHitboxes(minecraft);
        }
    }

    private void expandPlayerHitboxes(Minecraft minecraft) {
        for (PlayerEntity player : minecraft.world.getPlayers()) {
            if (player == minecraft.player) {
                continue;
            }
            float extra = hitboxExtra.getValue();
            float margin = 0.3f + extra;
            AxisAlignedBB expandedBox = new AxisAlignedBB(
                    player.getPosX() - margin,
                    player.getBoundingBox().minY,
                    player.getPosZ() - margin,
                    player.getPosX() + margin,
                    player.getBoundingBox().maxY,
                    player.getPosZ() + margin
            );
            player.setBoundingBox(expandedBox);
        }
    }

    private void resetAllPlayerHitboxes(Minecraft minecraft) {
        for (PlayerEntity player : minecraft.world.getPlayers()) {
            if (player == minecraft.player) {
                continue;
            }
            resetPlayerHitbox(player);
        }
    }

    private void resetPlayerHitbox(PlayerEntity player) {
        double halfWidth = player.getWidth() / 2.0;
        AxisAlignedBB defaultBox = new AxisAlignedBB(
                player.getPosX() - halfWidth,
                player.getPosY(),
                player.getPosZ() - halfWidth,
                player.getPosX() + halfWidth,
                player.getPosY() + player.getHeight(),
                player.getPosZ() + halfWidth
        );
        player.setBoundingBox(defaultBox);
    }

    private void resetAllPlayerHitboxes() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world == null) {
            return;
        }
        for (PlayerEntity player : minecraft.world.getPlayers()) {
            if (player == minecraft.player) {
                continue;
            }
            resetPlayerHitbox(player);
        }
    }
}
