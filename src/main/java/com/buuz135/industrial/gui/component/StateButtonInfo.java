package com.buuz135.industrial.gui.component;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class StateButtonInfo {

    private final int state;
    private final ResourceLocation texture;
    private final int textureX;
    private final int textureY;
    private final String[] tooltip;

    public StateButtonInfo(int state, ResourceLocation texture, int textureX, int textureY, String[] tooltip) {
        this.state = state;
        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;
        this.tooltip = new String[tooltip.length];
        for (int i = 0; i < tooltip.length; i++) {
            this.tooltip[i] = new TextComponentTranslation("conveyor.upgrade.industrialforegoing.tooltip." + tooltip[i]).getFormattedText();
        }
    }

    public int getState() {
        return state;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getTextureX() {
        return textureX;
    }

    public int getTextureY() {
        return textureY;
    }

    public String[] getTooltip() {
        return tooltip;
    }
}
