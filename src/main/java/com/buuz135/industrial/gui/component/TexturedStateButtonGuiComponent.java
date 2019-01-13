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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class TexturedStateButtonGuiComponent extends PositionedGuiComponent {

    private final int id;
    private StateButtonInfo[] buttonInfos;

    public TexturedStateButtonGuiComponent(int id, int x, int y, int xSize, int ySize, StateButtonInfo... buttonInfos) {
        super(x, y, xSize, ySize);
        this.id = id;
        this.buttonInfos = new StateButtonInfo[]{};
        if (buttonInfos != null) this.buttonInfos = buttonInfos;
    }

    @Override
    public void handleClick(GuiConveyor conveyor, int guiX, int guiY, int mouseX, int mouseY) {
        conveyor.sendMessage(id, new NBTTagCompound());
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void drawGuiBackgroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null) {
            GlStateManager.color(1, 1, 1, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(buttonInfo.getTexture());
            Minecraft.getMinecraft().currentScreen.drawTexturedModalRect(guiX + getXPos(), guiY + getYPos(), buttonInfo.getTextureX(), buttonInfo.getTextureY(), getXSize(), getYSize());
        }
    }

    @Override
    public void drawGuiForegroundLayer(int guiX, int guiY, int mouseX, int mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null && isInside(mouseX, mouseY)) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            Minecraft.getMinecraft().currentScreen.drawRect(getXPos() - guiX, getYPos() - guiY, getXPos() + getXSize() - guiX, getYPos() + getYSize() - guiY, -2130706433);
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
        }
    }

    @Nullable
    @Override
    public List<String> getTooltip(int guiX, int guiY, int mouseX, int mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null) {
            return Arrays.asList(buttonInfo.getTooltip());
        }
        return null;
    }

    public abstract int getState();

    private StateButtonInfo getStateInfo() {
        for (StateButtonInfo buttonInfo : buttonInfos) {
            if (buttonInfo.getState() == getState()) {
                return buttonInfo;
            }
        }
        return null;
    }
}
