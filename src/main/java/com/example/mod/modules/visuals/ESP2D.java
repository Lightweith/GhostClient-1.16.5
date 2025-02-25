package com.example.mod.modules.visuals;

import com.example.mod.modules.Category;
import com.example.mod.hooks.ForgeEventHook;
import com.example.mod.modules.obf.ObfuscatedModule;
import com.example.mod.settings.obf.ObfuscatedModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Arrays;

public class ESP2D extends ObfuscatedModule {
    private final ObfuscatedModeSetting espMode = new ObfuscatedModeSetting(
            new String[]{"E", "SPMo","d", "e"}, "Yel",
            Arrays.asList("Yel", "BlG", "BlW", "Blu")
    );

    public ESP2D() {
        super(new String[]{"2D"}, Category.BAY);
        addSetting(espMode);
    }

    @Override
    public void onEnable() {
        ForgeEventHook.register(this);
    }

    @Override
    public void onDisable() {
        ForgeEventHook.unregister(this);
    }

    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!isToggled())
            return;

        MatrixStack ms = event.getMatrixStack();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(ms.getLast().getMatrix());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Minecraft mc = Minecraft.getInstance();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player)
                continue;

            Vector3d camPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
            AxisAlignedBB playerBox = player.getBoundingBox();
            double x1 = playerBox.minX - camPos.x;
            double y1 = playerBox.minY - camPos.y;
            double z1 = playerBox.minZ - camPos.z;
            double x2 = playerBox.maxX - camPos.x;
            double y2 = playerBox.maxY - camPos.y;
            double z2 = playerBox.maxZ - camPos.z;

            renderESP(x1, y1, z1, x2, y2, z2);
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderESP(double x1, double y1, double z1, double x2, double y2, double z2) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.depthMask(false);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        // Stroke first, thicker
        GL11.glLineWidth(4.0f);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        Color outline = new Color(0, 0, 0, 255);
        drawBox(buffer, x1, y1, z1, x2, y2, z2, outline);
        Tessellator.getInstance().draw();

        // Then the main line
        GL11.glLineWidth(2.0f);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        Color mainCol = getThemedColor();
        drawBox(buffer, x1, y1, z1, x2, y2, z2, mainCol);
        Tessellator.getInstance().draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableTexture();
    }

    private void drawBox(BufferBuilder buf, double x1, double y1, double z1,
                         double x2, double y2, double z2, Color color) {
        // Drawing vertical lines
        addVertPair(buf, x1, y1, z1, x1, y2, z1, color);
        addVertPair(buf, x2, y1, z1, x2, y2, z1, color);
        addVertPair(buf, x1, y1, z2, x1, y2, z2, color);
        addVertPair(buf, x2, y1, z2, x2, y2, z2, color);

        // Bottom edge
        addVertPair(buf, x1, y1, z1, x2, y1, z1, color);
        addVertPair(buf, x1, y1, z2, x2, y1, z2, color);
        addVertPair(buf, x1, y1, z1, x1, y1, z2, color);
        addVertPair(buf, x2, y1, z1, x2, y1, z2, color);

        // Top edge
        addVertPair(buf, x1, y2, z1, x2, y2, z1, color);
        addVertPair(buf, x1, y2, z2, x2, y2, z2, color);
        addVertPair(buf, x1, y2, z1, x1, y2, z2, color);
        addVertPair(buf, x2, y2, z1, x2, y2, z2, color);
    }

    private void addVertPair(BufferBuilder buf, double x1, double y1, double z1,
                             double x2, double y2, double z2, Color color) {
        buf.pos(x1, y1, z1)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .endVertex();
        buf.pos(x2, y2, z2)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .endVertex();
    }

    private Color getThemedColor() {
        String mode = espMode.getValue();
        if ("BlG".equals(mode))
            return new Color(0, 255, 255);
        if ("BlW".equals(mode))
            return new Color(173, 216, 230);
        if ("Blu".equals(mode))
            return new Color(0, 0, 255);
        return new Color(255, 255, 0);
    }

}
