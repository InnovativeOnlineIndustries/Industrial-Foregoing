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
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

import javax.annotation.Nullable;
import java.util.List;

public abstract class RegulatorFilterGuiComponent extends PositionedGuiComponent {

    public static final ResourceLocation BG_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/conveyor.png");

    public RegulatorFilterGuiComponent(int x, int y, int xSize, int ySize) {
        super(x, y, xSize, ySize);
    }

    @Override
    public boolean handleClick(AbstractContainerScreen conveyor, int guiX, int guiY, double mouseX, double mouseY) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16) {
                    if (conveyor instanceof ICanSendNetworkMessage) {
                        ((ICanSendNetworkMessage) conveyor).sendMessage(pos, Minecraft.getInstance().player.inventory.getSelected().serializeNBT());
                    }
                    return true;
                }
                ++pos;
            }
        }
        return false;
    }

    @Override
    public boolean onScrolled(AbstractContainerScreen conveyor, int guiX, int guiY, double mouseX, double mouseY, double delta) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16) {
                    if (conveyor instanceof ICanSendNetworkMessage) {
                        CompoundTag compoundNBT = new CompoundTag();
                        int amount = (int) delta;
                        if (Screen.hasShiftDown()) {
                            amount *= getFilter().getSmallMultiplier();
                        }
                        if (Screen.hasControlDown()) {
                            amount *= getFilter().getBigMultiplier();
                        }
                        compoundNBT.putInt("Amount", amount);
                        ((ICanSendNetworkMessage) conveyor).sendMessage(pos, compoundNBT);
                    }
                    return true;
                }
                ++pos;
            }
        }
        return false;
    }

    @Override
    public void drawGuiBackgroundLayer(PoseStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                RenderSystem.setShaderTexture(0, BG_TEXTURE);
                Minecraft.getInstance().screen.blit(stack, posX, posY, 176, 0, 18, 18); //blit
                if (!getFilter().getFilter()[pos].getStack().isEmpty()) {
                    Minecraft.getInstance().getItemRenderer().renderGuiItem(getFilter().getFilter()[pos].getStack(), posX + 1, posY + 1);
                    if (isRegulator()) {
                        stack.pushPose();
                        stack.translate(0, 0, 260);
                        stack.scale(0.5f, 0.5f, 0.5f);
                        String amount = getFilter().getFilter()[pos].getAmount() + "";
                        Minecraft.getInstance().font.draw(stack, ChatFormatting.WHITE + amount, (posX + 17 - Minecraft.getInstance().font.width(amount) / 2f) * 2, (posY + 13) * 2, 0xFFFFFF);
                        stack.popPose();
                    }
                }
                ++pos;
            }
        }
    }

    @Override
    public void drawGuiForegroundLayer(PoseStack stack, int guiX, int guiY, double mouseX, double mouseY) {
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16) {
                    AssetUtil.drawSelectingOverlay(stack, posX + 1 - guiX, posY + 1 - guiY, posX + 17 - guiX, posY + 17 - guiY); //fill
                    return;
                }
            }
        }
    }

    @Override
    public boolean isInside(double mouseX, double mouseY) {
        return mouseX > getXPos() && mouseX < getXPos() + getXSize() * 18 && mouseY > getYPos() && mouseY < getYPos() + getYSize() * 18;
    }

    public abstract RegulatorFilter getFilter();

    public abstract boolean isRegulator();

    @Nullable
    @Override
    public List<Component> getTooltip(int guiX, int guiY, double mouseX, double mouseY) {
        int pos = 0;
        for (int i = 0; i < getYSize(); i++) {
            for (int x = 0; x < getXSize(); x++) {
                int posX = guiX + getXPos() + x * 18;
                int posY = guiY + getXPos() + i * 18;
                if (mouseX > posX + 1 && mouseX < posX + 1 + 16 && mouseY > posY + 1 && mouseY < posY + 1 + 16 && !getFilter().getFilter()[pos].getStack().isEmpty()) {
                    List<Component> strings = Minecraft.getInstance().screen.getTooltipFromItem(getFilter().getFilter()[pos].getStack());
                    if (isRegulator())
                        strings.add(new TextComponent(ChatFormatting.DARK_GRAY + "*Use Scroll Wheel to change*"));
                    return strings;
                }
                ++pos;
            }
        }
        return null;
    }
}
