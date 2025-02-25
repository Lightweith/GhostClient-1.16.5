package com.example.mod.modules.misc;

import com.example.mod.modules.Category;
import com.example.mod.modules.obf.ObfuscatedModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;

public class FakePlayer extends ObfuscatedModule {

    private RemoteClientPlayerEntity player = null;
    private Minecraft mc = Minecraft.getInstance();

    public FakePlayer() {
        super(new String[]{"Fa", "k", "epl", "a", "ye", "r"}, Category.OST);
    }

    @Override
    public void onEnable() {
        player = createPlayer();
    }

    @Override
    public void onDisable() {
        if (player != null) {
            player.remove();
        }
    }

    public RemoteClientPlayerEntity createPlayer() {
        double posX = mc.player.getPosX();
        double posY = mc.player.getPosY();
        double posZ = mc.player.getPosZ();

        float rotationYaw = mc.player.rotationYaw;
        float rotationPitch = mc.player.rotationPitch;

        RemoteClientPlayerEntity fakePlayer = new RemoteClientPlayerEntity(mc.world, mc.player.getGameProfile());

        fakePlayer.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
        fakePlayer.setHealth(20);
        fakePlayer.setSilent(false);

        mc.world.addPlayer(-1, fakePlayer);

        return fakePlayer;
    }
}
