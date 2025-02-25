package com.example.mod.modules.obf;

import com.example.mod.modules.Category;
import com.example.mod.modules.Module;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import java.util.ArrayList;
import java.util.List;

public abstract class ObfuscatedModule extends Module {
    private final List<ObfuscatedFragment> fragments = new ArrayList<>();

    public ObfuscatedModule(String[] parts, Category category) {
        super("", category);
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

    public void renderName(MatrixStack matrixStack, int x, int y, FontRenderer fontRenderer, int color) {
        int currentX = x;
        for (ObfuscatedFragment frag : fragments) {
            String original = frag.getOriginal();
            fontRenderer.drawStringWithShadow(matrixStack, original, currentX, y, color);
            currentX += fontRenderer.getStringWidth(original);
        }
    }
}
