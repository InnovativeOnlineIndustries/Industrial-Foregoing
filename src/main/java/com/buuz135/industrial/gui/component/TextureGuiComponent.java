/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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

import com.buuz135.industrial.api.conveyor.gui.PositionedGuiComponent;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TextureGuiComponent extends PositionedGuiComponent {

    private final ResourceLocation resourceLocation;
    private final int textureX;
    private final int textureY;
    private final List<ITextProperties> tooltip;

    public TextureGuiComponent(int x, int y, int xSize, int ySize, ResourceLocation resourceLocation, int textureX, int textureY, String... tooltip) {
        super(x, y, xSize, ySize);
        this.resourceLocation = resourceLocation;
        this.textureX = textureX;
        this.textureY = textureY;
        this.tooltip = new ArrayList<>();
        if (tooltip != null) {
            for (int i = 0; i < tooltip.length; i++) {
                this.tooltip.add(new TranslationTextComponent("conveyor.upgrade.industrialforegoing.tooltip." + tooltip[i]));
            }
        }
    }

    @Override
    public boolean handleClick(GuiConveyor conveyor, int guiX, int guiY, double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void drawGuiBackgroundLayer(MatrixStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
        Minecraft.getInstance().currentScreen.func_238474_b_(stack, guiX + getXPos(), guiY + getYPos(), textureX, textureY, getXSize(), getYSize());
    }

    @Override
    public void drawGuiForegroundLayer(MatrixStack stack, int guiX, int guiY, double mouseX, double mouseY) {

    }

    @Nullable
    @Override
    public List<ITextProperties> getTooltip(int guiX, int guiY, double mouseX, double mouseY) {
        return tooltip;
    }
}
