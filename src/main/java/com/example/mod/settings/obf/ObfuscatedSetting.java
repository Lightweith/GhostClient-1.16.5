package com.example.mod.settings.obf;

import com.example.mod.settings.Setting;
import com.example.mod.modules.obf.ObfuscatedFragment;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class ObfuscatedSetting<T> extends Setting<T> {
    private final List<ObfuscatedFragment> fragments = new ArrayList<>();

    public ObfuscatedSetting(String[] parts, T value) {
        super("", value);
        for (String part : parts) {
            fragments.add(new ObfuscatedFragment(part));
        }
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        for (ObfuscatedFragment frag : fragments) {
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
