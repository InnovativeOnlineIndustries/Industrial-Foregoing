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
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

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
    public boolean handleClick(GuiConveyor conveyor, int guiX, int guiY, double mouseX, double mouseY) {
        conveyor.sendMessage(id, new CompoundNBT());
        Minecraft.getInstance().getSoundHandler().play(new SimpleSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getInstance().player.getPosition()));//getPos
        return true;
    }

    @Override
    public void drawGuiBackgroundLayer(MatrixStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null) {
            RenderSystem.color4f(1, 1, 1, 1);
            Minecraft.getInstance().getTextureManager().bindTexture(buttonInfo.getTexture());
            Minecraft.getInstance().currentScreen.blit(stack, guiX + getXPos(), guiY + getYPos(), buttonInfo.getTextureX(), buttonInfo.getTextureY(), getXSize(), getYSize()); //blit
        }
    }

    @Override
    public void drawGuiForegroundLayer(MatrixStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null && isInside(mouseX, mouseY)) {
            RenderSystem.disableLighting();
            RenderSystem.enableDepthTest();
            AbstractGui.fill(stack, getXPos() - guiX, getYPos() - guiY, getXPos() + getXSize() - guiX, getYPos() + getYSize() - guiY, -2130706433);//fill
            RenderSystem.enableLighting();
            RenderSystem.disableAlphaTest();
        }
    }

    @Nullable
    @Override
    public List<ITextComponent> getTooltip(int guiX, int guiY, double mouseX, double mouseY) {
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
