package com.example.mod.settings.obf;

import com.example.mod.settings.FloatSetting;
import com.example.mod.modules.obf.ObfuscatedFragment;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import java.util.ArrayList;
import java.util.List;

public class ObfuscatedFloatSetting extends FloatSetting {
    private final List<ObfuscatedFragment> fragments = new ArrayList<>();

    public ObfuscatedFloatSetting(String[] parts, Float value, float min, float max) {
        super(getObfuscatedName(parts), value, min, max);
        for (String part : parts) {
            fragments.add(new ObfuscatedFragment(part));
        }
    }

    private static String getObfuscatedName(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            ObfuscatedFragment frag = new ObfuscatedFragment(part);
            sb.append(frag.getObfuscated());
        }
        return sb.toString();
    }

    public String getOriginalName() {
        StringBuilder sb = new StringBuilder();
        for (ObfuscatedFragment frag : fragments) {
            sb.append(frag.getOriginal());
        }
        return sb.toString();
    }

    public void renderName(MatrixStack matrixStack, int x, int y, FontRenderer fontRenderer, int color) {
        int currentX = x;
        for (ObfuscatedFragment frag : fragments) {
            String original = frag.getOriginal();
            fontRenderer.drawStringWithShadow(matrixStack, original, currentX, y, color);
            currentX += fontRenderer.getStringWidth(original);
        }
    }
}
