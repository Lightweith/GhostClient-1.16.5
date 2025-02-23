package com.example.mod.ui;

import com.example.mod.modules.Category;
import com.example.mod.modules.Module;
import com.example.mod.modules.ModuleManager;
import com.example.mod.settings.BooleanSetting;
import com.example.mod.settings.FloatSetting;
import com.example.mod.settings.ModeSetting;
import com.example.mod.settings.Setting;
import com.example.mod.modules.obf.ObfuscatedModule;
import com.example.mod.settings.obf.ObfuscatedBooleanSetting;
import com.example.mod.settings.obf.ObfuscatedFloatSetting;
import com.example.mod.settings.obf.ObfuscatedModeSetting;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class UI extends Screen {

    private ModuleManager moduleManager;
    private Category currentCategory = Category.KCH;

    private int guiWidth = 320;
    private int guiHeight = 240;

    private int guiX;
    private int guiY;

    private float moduleListOffset = 300;

    private FontRenderer fontRenderer;

    private Color accentColor = new Color(0xFF6200EE, true);
    private Color accentLightColor = new Color(0xFFBB86FC, true);
    private Color backgroundDark = new Color(0xFF121212, true);
    private Color backgroundLight = new Color(0xFF1E1E1E, true);
    private Color textColor = new Color(0xFFFFFFFF, true);
    private Color textColorDark = new Color(0xB3FFFFFF, true);

    private FloatSetting activeSlider = null;
    private int activeSliderX, activeSliderWidth;

    private boolean draggingGui = false;
    private int dragOffsetX, dragOffsetY;
    private boolean draggingScroll = false;
    private int scrollDragOffsetY = 0;

    private int scrollOffset = 0;
    private float scrollAnimation = 0;
    private float categoryAnimation = 0;

    public UI(ModuleManager moduleManager) {
        super(new StringTextComponent(""));
        this.moduleManager = moduleManager;
        this.guiX = (Minecraft.getInstance().getMainWindow().getScaledWidth() - guiWidth) / 2;
        this.guiY = (Minecraft.getInstance().getMainWindow().getScaledHeight() - guiHeight) / 2;
        this.fontRenderer = Minecraft.getInstance().fontRenderer;
    }

    private int getModuleAreaX() {
        return guiX + 10 - (int) moduleListOffset + (int) categoryAnimation;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        scrollAnimation += (scrollOffset - scrollAnimation) * 0.5f;
        categoryAnimation += (0 - categoryAnimation) * 0.2f;

        drawRoundedRect(matrixStack, guiX, guiY, guiX + guiWidth, guiY + guiHeight, 10, backgroundLight.getRGB());

        String title = "";
        int titleWidth = fontRenderer.getStringWidth(title);
        drawGradientRect(matrixStack, guiX, guiY, guiX + guiWidth, guiY + 40, 0xFF000000, 0x00000000);
        fontRenderer.drawStringWithShadow(matrixStack, title, guiX + (guiWidth - titleWidth) / 2, guiY + 10, accentColor.getRGB());

        drawFancyDragHandle(matrixStack, guiX + 4, guiY + 4, 20, 20, accentColor.getRGB(), accentLightColor.getRGB());

        int tabWidth = guiWidth / Category.values().length;
        int tabHeight = 30;
        for (int i = 0; i < Category.values().length; i++) {
            Category cat = Category.values()[i];
            int tabX = guiX + i * tabWidth;
            int tabY = guiY + 40;
            Color baseTabColor = (cat == currentCategory) ? accentColor : backgroundLight;
            if (!cat.equals(currentCategory) && isHovered(mouseX, mouseY, tabX, tabY, tabWidth, tabHeight)) {
                baseTabColor = baseTabColor.brighter();
            }
            drawFancyTab(matrixStack, tabX, tabY, tabWidth, tabHeight, baseTabColor.getRGB(), cat == currentCategory);

            String text = cat.getName();
            int textWidth = fontRenderer.getStringWidth(text);
            int textX = tabX + (tabWidth - textWidth) / 2;
            int textY = tabY + (tabHeight - fontRenderer.FONT_HEIGHT) / 2;
            fontRenderer.drawStringWithShadow(matrixStack, text, textX, textY, textColor.getRGB());
        }

        if (moduleListOffset > 0) {
            float animationSpeed = 15f;
            moduleListOffset -= animationSpeed;
            if (moduleListOffset < 0) moduleListOffset = 0;
        }

        int scrollBarWidth = 6;
        int moduleAreaX = getModuleAreaX();
        int moduleAreaY = guiY + 80;
        int moduleAreaWidth = guiWidth - 20 - scrollBarWidth - 2;
        int visibleHeight = guiHeight - 90;

        enableScissor(moduleAreaX, moduleAreaY, moduleAreaWidth, visibleHeight);

        int contentHeight = getContentHeight();
        int maxScroll = Math.max(contentHeight - visibleHeight, 0);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        List<Module> modules = moduleManager.getModulesByCategory(currentCategory);
        int moduleButtonHeight = 30;
        int yOffset = 0;
        for (Module module : modules) {
            int btnY = moduleAreaY - (int) scrollAnimation + yOffset;

            Color moduleButtonColor = module.isToggled() ? accentColor : accentLightColor;
            if (isHovered(mouseX, mouseY, moduleAreaX, btnY, moduleAreaWidth, moduleButtonHeight)) {
                moduleButtonColor = moduleButtonColor.brighter();
            }
            drawFancyButton(matrixStack, moduleAreaX, btnY, moduleAreaWidth, moduleButtonHeight, moduleButtonColor.getRGB());

            String moduleName = module.getName();
            int textWidth = fontRenderer.getStringWidth(moduleName);
            int textX = moduleAreaX + 10;
            int textY = btnY + (moduleButtonHeight - fontRenderer.FONT_HEIGHT) / 2;
            if (module instanceof ObfuscatedModule) {
                ((ObfuscatedModule) module).renderName(matrixStack, textX, textY, fontRenderer, textColor.getRGB());
            } else {
                fontRenderer.drawStringWithShadow(matrixStack, module.getName(), textX, textY, textColor.getRGB());
            }

            String toggleText = module.isToggled() ? "on" : "off";
            int toggleWidth = fontRenderer.getStringWidth(toggleText);
            int toggleX = moduleAreaX + moduleAreaWidth - toggleWidth - 10;
            int toggleY = btnY + (moduleButtonHeight - fontRenderer.FONT_HEIGHT) / 2;
            Color toggleColor = module.isToggled() ? accentColor : textColorDark;
            fontRenderer.drawStringWithShadow(matrixStack, toggleText, toggleX, toggleY, toggleColor.getRGB());

            yOffset += moduleButtonHeight + 5;

            if (module.isToggled()) {
                for (Setting<?> setting : module.getSettings()) {
                    int settingY = moduleAreaY - (int) scrollAnimation + yOffset;
                    if (setting instanceof ObfuscatedBooleanSetting) {
                        ObfuscatedBooleanSetting obfBool = (ObfuscatedBooleanSetting) setting;
                        int settingHeight = 24;
                        int settingX = moduleAreaX + 10;
                        drawFancyPanel(matrixStack, settingX, settingY, moduleAreaWidth - 20, settingHeight, backgroundDark.getRGB());
                        obfBool.renderName(matrixStack, settingX + 5, settingY + 5, fontRenderer, textColorDark.getRGB());
                        int nameWidth = fontRenderer.getStringWidth(obfBool.getOriginalName());
                        String valueText = ": " + (obfBool.getValue() ? "ON" : "OFF");
                        fontRenderer.drawStringWithShadow(matrixStack, valueText, settingX + 5 + nameWidth, settingY + 5, textColorDark.getRGB());
                        yOffset += settingHeight + 5;
                    }
                    else if (setting instanceof BooleanSetting) {
                        int settingHeight = 24;
                        int settingX = moduleAreaX + 10;
                        drawFancyPanel(matrixStack, settingX, settingY, moduleAreaWidth - 20, settingHeight, backgroundDark.getRGB());
                        String settingText = setting.getName() + ": " + (((BooleanSetting) setting).getValue() ? "ON" : "OFF");
                        fontRenderer.drawStringWithShadow(matrixStack, settingText, settingX + 5, settingY + 5, textColorDark.getRGB());
                        yOffset += settingHeight + 5;
                    }
                    else if (setting instanceof ObfuscatedFloatSetting) {
                        ObfuscatedFloatSetting obfFloat = (ObfuscatedFloatSetting) setting;
                        int sliderX = moduleAreaX + 10;
                        int sliderWidth = moduleAreaWidth - 20;
                        int sliderBarY = settingY + fontRenderer.FONT_HEIGHT + 5;
                        int sliderHeight = 6;

                        drawFancyPanel(matrixStack, sliderX, sliderBarY - 2, sliderWidth, sliderHeight + 4, backgroundDark.getRGB());

                        float value = obfFloat.getValue();
                        float min = obfFloat.getMin();
                        float max = obfFloat.getMax();
                        float percentage = (value - min) / (max - min);
                        int filledWidth = (int) (sliderWidth * percentage);
                        fill(matrixStack, sliderX, sliderBarY, sliderX + filledWidth, sliderBarY + sliderHeight, accentColor.getRGB());

                        obfFloat.renderName(matrixStack, sliderX + 5, settingY, fontRenderer, textColorDark.getRGB());
                        int nameWidth = fontRenderer.getStringWidth(obfFloat.getOriginalName());
                        String valueText = ": " + String.format("%.2f", value);
                        fontRenderer.drawStringWithShadow(matrixStack, valueText, sliderX + 5 + nameWidth, settingY, textColorDark.getRGB());
                        yOffset += fontRenderer.FONT_HEIGHT + sliderHeight + 10;
                    }
                    else if (setting instanceof FloatSetting) {
                        FloatSetting floatSetting = (FloatSetting) setting;
                        int sliderX = moduleAreaX + 10;
                        int sliderWidth = moduleAreaWidth - 20;
                        int sliderBarY = settingY + fontRenderer.FONT_HEIGHT + 5;
                        int sliderHeight = 6;

                        drawFancyPanel(matrixStack, sliderX, sliderBarY - 2, sliderWidth, sliderHeight + 4, backgroundDark.getRGB());

                        float value = floatSetting.getValue();
                        float min = floatSetting.getMin();
                        float max = floatSetting.getMax();
                        float percentage = (value - min) / (max - min);
                        int filledWidth = (int) (sliderWidth * percentage);
                        fill(matrixStack, sliderX, sliderBarY, sliderX + filledWidth, sliderBarY + sliderHeight, accentColor.getRGB());

                        String settingText = setting.getName() + ": " + String.format("%.2f", value);
                        fontRenderer.drawStringWithShadow(matrixStack, settingText, sliderX, settingY, textColorDark.getRGB());
                        yOffset += fontRenderer.FONT_HEIGHT + sliderHeight + 10;
                    }
                    else if (setting instanceof ObfuscatedModeSetting) {
                        ObfuscatedModeSetting obfMode = (ObfuscatedModeSetting) setting;
                        int modeX = moduleAreaX + 10;
                        int modeY = settingY;
                        int modeWidth = moduleAreaWidth - 20;
                        int modeHeight = 24;
                        drawFancyPanel(matrixStack, modeX, modeY, modeWidth, modeHeight, backgroundDark.getRGB());
                        obfMode.renderName(matrixStack, modeX + 5, modeY + 5, fontRenderer, textColorDark.getRGB());
                        int nameWidth = fontRenderer.getStringWidth(obfMode.getOriginalName());
                        String settingText = ": " + obfMode.getValue();
                        fontRenderer.drawStringWithShadow(matrixStack, settingText, modeX + 5 + nameWidth, modeY + 5, textColorDark.getRGB());
                        String arrows = "< >";
                        int arrowsWidth = fontRenderer.getStringWidth(arrows);
                        fontRenderer.drawStringWithShadow(matrixStack, arrows, modeX + modeWidth - arrowsWidth - 5, modeY + 5, accentColor.getRGB());
                        yOffset += modeHeight + 5;
                    }
                    else if (setting instanceof ModeSetting) {
                        int modeX = moduleAreaX + 10;
                        int modeY = settingY;
                        int modeWidth = moduleAreaWidth - 20;
                        int modeHeight = 24;
                        drawFancyPanel(matrixStack, modeX, modeY, modeWidth, modeHeight, backgroundDark.getRGB());
                        ModeSetting modeSetting = (ModeSetting) setting;
                        String settingText = setting.getName() + ": " + modeSetting.getValue();
                        fontRenderer.drawStringWithShadow(matrixStack, settingText, modeX + 5, modeY + 5, textColorDark.getRGB());
                        String arrows = "< >";
                        int arrowsWidth = fontRenderer.getStringWidth(arrows);
                        fontRenderer.drawStringWithShadow(matrixStack, arrows, modeX + modeWidth - arrowsWidth - 5, modeY + 5, accentColor.getRGB());
                        yOffset += modeHeight + 5;
                    }
                }
            }
        }

        disableScissor();

        if (maxScroll > 0) {
            int scrollBarX = moduleAreaX + moduleAreaWidth + 2;
            int scrollBarY = moduleAreaY;
            int scrollBarHeight = visibleHeight;
            drawFancyPanel(matrixStack, scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight, backgroundDark.getRGB());
            int thumbHeight = (int) ((float) visibleHeight * visibleHeight / contentHeight);
            if (thumbHeight < 20) thumbHeight = 20;
            int thumbY = scrollBarY;
            if (maxScroll != 0) {
                thumbY = scrollBarY + (int) ((float) scrollAnimation / maxScroll * (scrollBarHeight - thumbHeight));
            }
            drawGradientRect(matrixStack, scrollBarX, thumbY, scrollBarX + scrollBarWidth, thumbY + thumbHeight, accentColor.getRGB(), accentLightColor.getRGB());
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int scrollStep = 15;
        scrollOffset -= delta * scrollStep;
        int visibleHeight = guiHeight - 90;
        int contentHeight = getContentHeight();
        int maxScroll = Math.max(contentHeight - visibleHeight, 0);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, guiX + 4, guiY + 4, 20, 20)) {
            draggingGui = true;
            dragOffsetX = (int) mouseX - guiX;
            dragOffsetY = (int) mouseY - guiY;
            return true;
        }

        int tabWidth = guiWidth / Category.values().length;
        int tabHeight = 30;
        for (int i = 0; i < Category.values().length; i++) {
            int tabX = guiX + i * tabWidth;
            int tabY = guiY + 40;
            if (mouseX >= tabX && mouseX <= tabX + tabWidth && mouseY >= tabY && mouseY <= tabY + tabHeight) {
                if (currentCategory != Category.values()[i]) {
                    currentCategory = Category.values()[i];
                    categoryAnimation = 300;
                    scrollOffset = 0;
                }
                return true;
            }
        }

        int scrollBarWidth = 6;
        int moduleAreaX = getModuleAreaX();
        int moduleAreaY = guiY + 80;
        int moduleAreaWidth = guiWidth - 20 - scrollBarWidth - 2;
        int visibleHeight = guiHeight - 90;

        int contentHeight = getContentHeight();
        int maxScroll = Math.max(contentHeight - visibleHeight, 0);
        if (maxScroll > 0) {
            int scrollBarX = moduleAreaX + moduleAreaWidth + 2;
            int scrollBarY = moduleAreaY;
            int scrollBarHeight = visibleHeight;
            int thumbHeight = (int) ((float) visibleHeight * visibleHeight / contentHeight);
            if (thumbHeight < 20) thumbHeight = 20;
            int thumbY = scrollBarY;
            if (maxScroll != 0) {
                thumbY = scrollBarY + (int) ((float) scrollOffset / maxScroll * (scrollBarHeight - thumbHeight));
            }
            if (isHovered(mouseX, mouseY, scrollBarX, thumbY, scrollBarWidth, thumbHeight)) {
                draggingScroll = true;
                scrollDragOffsetY = (int) mouseY - thumbY;
                return true;
            }
        }

        int moduleButtonHeight = 30;
        int baseY = moduleAreaY - (int) scrollAnimation;
        int yOffset = 0;
        List<Module> modules = moduleManager.getModulesByCategory(currentCategory);
        for (Module module : modules) {
            int btnY = baseY + yOffset;
            if (mouseX >= moduleAreaX && mouseX <= moduleAreaX + moduleAreaWidth &&
                    mouseY >= btnY && mouseY <= btnY + moduleButtonHeight) {
                module.toggle();
                return true;
            }
            yOffset += moduleButtonHeight + 5;
            if (module.isToggled()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof ObfuscatedBooleanSetting) {
                        int settingHeight = 24;
                        int settingX = moduleAreaX + 10;
                        int settingY = baseY + yOffset;
                        if (mouseX >= settingX && mouseX <= settingX + moduleAreaWidth - 20 &&
                                mouseY >= settingY && mouseY <= settingY + settingHeight) {
                            ObfuscatedBooleanSetting obfBool = (ObfuscatedBooleanSetting) setting;
                            obfBool.setValue(!obfBool.getValue());
                            return true;
                        }
                        yOffset += settingHeight + 5;
                    }
                    else if (setting instanceof BooleanSetting) {
                        int settingHeight = 24;
                        int settingX = moduleAreaX + 10;
                        int settingY = baseY + yOffset;
                        if (mouseX >= settingX && mouseX <= settingX + moduleAreaWidth - 20 &&
                                mouseY >= settingY && mouseY <= settingY + settingHeight) {
                            BooleanSetting boolSetting = (BooleanSetting) setting;
                            boolSetting.setValue(!boolSetting.getValue());
                            return true;
                        }
                        yOffset += settingHeight + 5;
                    }
                    else if (setting instanceof ObfuscatedFloatSetting) {
                        ObfuscatedFloatSetting obfFloat = (ObfuscatedFloatSetting) setting;
                        int sliderX = moduleAreaX + 10;
                        int sliderWidth = moduleAreaWidth - 20;
                        int sliderBarY = baseY + yOffset + fontRenderer.FONT_HEIGHT + 5;
                        int sliderHeight = 6;
                        if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
                                mouseY >= sliderBarY && mouseY <= sliderBarY + sliderHeight) {
                            activeSlider = obfFloat;
                            activeSliderX = sliderX;
                            activeSliderWidth = sliderWidth;
                            updateFloatSetting(mouseX);
                            return true;
                        }
                        yOffset += fontRenderer.FONT_HEIGHT + sliderHeight + 10;
                    }
                    else if (setting instanceof FloatSetting) {
                        FloatSetting floatSetting = (FloatSetting) setting;
                        int sliderX = moduleAreaX + 10;
                        int sliderWidth = moduleAreaWidth - 20;
                        int sliderBarY = baseY + yOffset + fontRenderer.FONT_HEIGHT + 5;
                        int sliderHeight = 6;
                        if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
                                mouseY >= sliderBarY && mouseY <= sliderBarY + sliderHeight) {
                            activeSlider = floatSetting;
                            activeSliderX = sliderX;
                            activeSliderWidth = sliderWidth;
                            updateFloatSetting(mouseX);
                            return true;
                        }
                        yOffset += fontRenderer.FONT_HEIGHT + sliderHeight + 10;
                    }
                    else if (setting instanceof ObfuscatedModeSetting) {
                        int modeX = moduleAreaX + 10;
                        int modeY = baseY + yOffset;
                        int modeWidth = moduleAreaWidth - 20;
                        int modeHeight = 24;
                        if (mouseX >= modeX && mouseX <= modeX + modeWidth &&
                                mouseY >= modeY && mouseY <= modeY + modeHeight) {
                            ObfuscatedModeSetting obfMode = (ObfuscatedModeSetting) setting;
                            int index = obfMode.getModes().indexOf(obfMode.getValue());
                            index = (index + 1) % obfMode.getModes().size();
                            obfMode.setValue(obfMode.getModes().get(index));
                            return true;
                        }
                        yOffset += modeHeight + 5;
                    }
                    else if (setting instanceof ModeSetting) {
                        int modeX = moduleAreaX + 10;
                        int modeY = baseY + yOffset;
                        int modeWidth = moduleAreaWidth - 20;
                        int modeHeight = 24;
                        if (mouseX >= modeX && mouseX <= modeX + modeWidth &&
                                mouseY >= modeY && mouseY <= modeY + modeHeight) {
                            ModeSetting modeSetting = (ModeSetting) setting;
                            int index = modeSetting.getModes().indexOf(modeSetting.getValue());
                            index = (index + 1) % modeSetting.getModes().size();
                            modeSetting.setValue(modeSetting.getModes().get(index));
                            return true;
                        }
                        yOffset += modeHeight + 5;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateFloatSetting(double mouseX) {
        if (activeSlider != null) {
            float min = activeSlider.getMin();
            float max = activeSlider.getMax();
            float percentage = (float) (mouseX - activeSliderX) / activeSliderWidth;
            if (percentage < 0) percentage = 0;
            if (percentage > 1) percentage = 1;
            float newValue = min + percentage * (max - min);
            activeSlider.setValue(newValue);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingGui) {
            guiX = (int) mouseX - dragOffsetX;
            guiY = (int) mouseY - dragOffsetY;
            return true;
        }

        if (draggingScroll) {
            int scrollBarWidth = 6;
            int moduleAreaX = getModuleAreaX();
            int moduleAreaY = guiY + 80;
            int moduleAreaWidth = guiWidth - 20 - scrollBarWidth - 2;
            int visibleHeight = guiHeight - 90;
            int contentHeight = getContentHeight();
            int maxScroll = Math.max(contentHeight - visibleHeight, 0);
            int scrollBarY = moduleAreaY;
            int scrollBarHeight = visibleHeight;
            int thumbHeight = (int) ((float) visibleHeight * visibleHeight / contentHeight);
            if (thumbHeight < 20) thumbHeight = 20;
            int newThumbY = (int) mouseY - scrollDragOffsetY;
            if (newThumbY < scrollBarY) newThumbY = scrollBarY;
            if (newThumbY > scrollBarY + scrollBarHeight - thumbHeight)
                newThumbY = scrollBarY + scrollBarHeight - thumbHeight;
            scrollOffset = (int) (((float) (newThumbY - scrollBarY) / (scrollBarHeight - thumbHeight)) * maxScroll);
            return true;
        }
        if (activeSlider != null) {
            updateFloatSetting(mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingGui = false;
        draggingScroll = false;
        activeSlider = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawRoundedRect(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int radius, int color) {
        fill(matrixStack, x1 + radius, y1, x2 - radius, y2, color);
        fill(matrixStack, x1, y1 + radius, x1 + radius, y2 - radius, color);
        fill(matrixStack, x2 - radius, y1 + radius, x2, y2 - radius, color);

        fillCircle(matrixStack, x1 + radius, y1 + radius, radius, color);
        fillCircle(matrixStack, x2 - radius, y1 + radius, radius, color);
        fillCircle(matrixStack, x1 + radius, y2 - radius, radius, color);
        fillCircle(matrixStack, x2 - radius, y2 - radius, radius, color);
    }

    private void fillCircle(MatrixStack matrixStack, int centerX, int centerY, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    fill(matrixStack, centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
    }

    private void drawFancyDragHandle(MatrixStack matrixStack, int x, int y, int width, int height,
                                     int fillColor, int borderColor) {
        drawRoundedRect(matrixStack, x, y, x + width, y + height, 5, fillColor);

        int lineCount = 3;
        int lineHeight = 2;
        int lineSpacing = 4;
        int lineWidth = width - 8;
        int centerY = y + height / 2;
        int startX = x + (width - lineWidth) / 2;

        int lineY = centerY - lineSpacing;
        fill(matrixStack, startX, lineY, startX + lineWidth, lineY + lineHeight, borderColor);

        lineY = centerY - (lineHeight / 2);
        fill(matrixStack, startX, lineY, startX + lineWidth, lineY + lineHeight, borderColor);

        lineY = centerY + lineSpacing - (lineHeight / 2);
        fill(matrixStack, startX, lineY, startX + lineWidth, lineY + lineHeight, borderColor);
    }

    private boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private int getContentHeight() {
        int height = 0;
        int moduleButtonHeight = 30;
        List<Module> modules = moduleManager.getModulesByCategory(currentCategory);
        for (Module module : modules) {
            height += moduleButtonHeight + 5;
            if (module.isToggled()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof BooleanSetting) {
                        height += 24 + 5;
                    } else if (setting instanceof FloatSetting) {
                        height += fontRenderer.FONT_HEIGHT + 6 + 10;
                    } else if (setting instanceof ModeSetting) {
                        height += 24 + 5;
                    }
                }
            }
        }
        return height;
    }

    private void drawGradientRect(MatrixStack matrixStack, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;

        float endAlpha = (float)(endColor >> 24 & 255) / 255.0F;
        float endRed   = (float)(endColor >> 16 & 255) / 255.0F;
        float endGreen = (float)(endColor >>  8 & 255) / 255.0F;
        float endBlue  = (float)(endColor       & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        bufferbuilder.pos(matrix, right, top, 0)
                .color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.pos(matrix, left, top, 0)
                .color(startRed, startGreen, startBlue, startAlpha).endVertex();
        bufferbuilder.pos(matrix, left, bottom, 0)
                .color(endRed, endGreen, endBlue, endAlpha).endVertex();
        bufferbuilder.pos(matrix, right, bottom, 0)
                .color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private void drawFancyPanel(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        fill(matrixStack, x + 1, y + 1, x + width - 1, y + height - 1, color);
        drawGradientRect(matrixStack, x, y, x + width, y + 1, 0x60FFFFFF, 0x00FFFFFF);
        drawGradientRect(matrixStack, x, y + height - 1, x + width, y + height, 0x00000000, 0x60000000);
    }

    private void drawFancyButton(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        drawFancyPanel(matrixStack, x, y, width, height, color);
        drawGradientRect(matrixStack, x, y, x + width, y + height / 2, 0x30FFFFFF, 0x00FFFFFF);
    }

    private void drawFancyTab(MatrixStack matrixStack, int x, int y, int width, int height, int color, boolean isActive) {
        drawFancyPanel(matrixStack, x, y, width, height, color);
        if (isActive) {
            drawGradientRect(matrixStack, x, y + height - 2, x + width, y + height, accentColor.getRGB(), accentLightColor.getRGB());
        }
    }

    private void enableScissor(int x, int y, int width, int height) {
        double scale = Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(x * scale), (int)(Minecraft.getInstance().getMainWindow().getFramebufferHeight() - (y + height) * scale),
                (int)(width * scale), (int)(height * scale));
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
