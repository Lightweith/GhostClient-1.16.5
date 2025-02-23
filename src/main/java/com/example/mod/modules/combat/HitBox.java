package com.example.mod.modules.combat;

import com.example.mod.modules.Category;
import com.example.mod.modules.obf.ObfuscatedModule;
import com.example.mod.settings.obf.ObfuscatedFloatSetting;
import com.example.mod.hooks.ForgeEventHook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class HitBox extends ObfuscatedModule {

    private final ObfuscatedFloatSetting expand = new ObfuscatedFloatSetting(new String[]{"set", "tin", "gs"}, 0.3f, 0f, 10f);

    public HitBox() {
        super(new String[]{"Hit", "Bo", "x"}, Category.KCH);
        addSetting(expand);
    }

    @Override
    public void onEnable() {
        ForgeEventHook.register(this);
    }

    @Override
    public void onDisable() {
        ForgeEventHook.unregister(this);
    }

    @Override
    public void update() {
    }

    public void onRenderPlayer(RenderPlayerEvent event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (event.getPlayer() == Minecraft.getInstance().player) {
            return;
        }
        float size = expand.getValue();
        Entity target = event.getPlayer();
        AxisAlignedBB newBB = new AxisAlignedBB(
                target.getPosX() - size,
                target.getBoundingBox().minY,
                target.getPosZ() - size,
                target.getPosX() + size,
                target.getBoundingBox().maxY,
                target.getPosZ() + size
        );
        target.setBoundingBox(newBB);
    }
}
