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
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FilterGuiComponent extends PositionedGuiComponent {

    public static final ResourceLocation BG_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/conveyor.png");

    public FilterGuiComponent(int x, int y, int xSize, int ySize) {
        super(x, y, xSize, ySize);
    }

    @Override
    public void handleClick(GuiConveyor conveyor, int guiX, int guiY, int mouseX, int mouseY) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16) {
                    conveyor.sendMessage(pos, Minecraft.getMinecraft().player.inventory.getItemStack().serializeNBT());
                    return;
                }
                ++pos;
            }
        }
    }

    @Override
    public void drawGuiBackgroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                Minecraft.getMinecraft().getTextureManager().bindTexture(BG_TEXTURE);
                Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(posX, posY, 176, 0, 18, 18);
                if (!getFilter().getFilter()[pos].getStack().isEmpty()) {
                    RenderHelper.enableGUIStandardItemLighting();
                    Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(getFilter().getFilter()[pos].getStack(), posX + 1, posY + 1);
                }
                ++pos;
            }
        }
    }

    @Override
    public void drawGuiForegroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16) {
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    Minecraft.getMinecraft().currentScreen.drawRect(posX + 1 - guiX, posY + 1 - guiY, posX + 17 - guiX, posY + 17 - guiY, -2130706433);
                    GlStateManager.enableLighting();
                    GlStateManager.disableDepth();
                    return;
                }
            }
        }
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > getXPos() && mouseX < getXPos() + getXSize() * 18 && mouseY > getYPos() && mouseY < getYPos() + getYSize() * 18;
    }

    public abstract IFilter getFilter();

    @Nullable
    @Override
    public List<String> getTooltip(int guiX, int guiY, int mouseX, int mouseY) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16 && !getFilter().getFilter()[pos].getStack().isEmpty()) {
                    return Minecraft.getMinecraft().currentScreen.getItemToolTip(getFilter().getFilter()[pos].getStack());
                }
                ++pos;
            }
        }
        return null;
    }
}
