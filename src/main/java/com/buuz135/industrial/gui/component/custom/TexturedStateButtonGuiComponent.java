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
package com.buuz135.industrial.gui.component.custom;

import com.buuz135.industrial.api.conveyor.gui.PositionedGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;

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
    public boolean handleClick(AbstractContainerScreen conveyor, int guiX, int guiY, double mouseX, double mouseY) {
        if (conveyor instanceof ICanSendNetworkMessage) {
            ((ICanSendNetworkMessage) conveyor).sendMessage(id, new CompoundTag());
        }
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 1.0F, 1.0F, Minecraft.getInstance().player.blockPosition()));//getPos
        return true;
    }

    @Override
    public void drawGuiBackgroundLayer(PoseStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null) {
//            RenderSystem.color4f(1, 1, 1, 1);
//            Minecraft.getInstance().getTextureManager().bind(buttonInfo.getTexture());
            Minecraft.getInstance().screen.blit(stack, guiX + getXPos(), guiY + getYPos(), buttonInfo.getTextureX(), buttonInfo.getTextureY(), getXSize(), getYSize()); //blit
        }
    }

    @Override
    public void drawGuiForegroundLayer(PoseStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        StateButtonInfo buttonInfo = getStateInfo();
        if (buttonInfo != null && isInside(mouseX, mouseY)) {
//            RenderSystem.disableLighting();
//            RenderSystem.enableDepthTest();
            GuiComponent.fill(stack, getXPos() - guiX, getYPos() - guiY, getXPos() + getXSize() - guiX, getYPos() + getYSize() - guiY, -2130706433);//fill
//            RenderSystem.enableLighting();
//            RenderSystem.disableAlphaTest();
        }
    }

    @Nullable
    @Override
    public List<Component> getTooltip(int guiX, int guiY, double mouseX, double mouseY) {
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
