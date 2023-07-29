package com.buuz135.industrial.plugin.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public abstract class CustomJeiDrawable implements IDrawable {

    private final int width;
    private final int height;

    public CustomJeiDrawable(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

}
