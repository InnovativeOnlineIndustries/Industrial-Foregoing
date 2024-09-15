/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.gui.component;

import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TextureScreenAddon extends BasicScreenAddon {

    private final ResourceLocation resourceLocation;
    private final int textureX;
    private final int textureY;
    private final int xSize;
    private final int ySize;

    public TextureScreenAddon(int x, int y, int xSize, int ySize, ResourceLocation resourceLocation, int textureX, int textureY) {
        super(x, y);
        this.resourceLocation = resourceLocation;
        this.textureX = textureX;
        this.textureY = textureY;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public List<Component> getTooltipLines() {
        return super.getTooltipLines();
    }


    @Override
    public int getXSize() {
        return this.xSize;
    }

    @Override
    public int getYSize() {
        return this.ySize;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(resourceLocation, guiX + getPosX(), guiY + getPosY(), textureX, textureY, getXSize(), getYSize());

    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3, float v) {

    }
}
