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

import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.NumberUtils;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.addon.BasicButtonAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.button.ButtonComponent;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class SlotDefinitionGuiAddon extends BasicButtonAddon {

    private int slotId;

    public SlotDefinitionGuiAddon(ButtonComponent buttonComponent, int slotId) {
        super(buttonComponent);
        this.slotId = slotId;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (!getItemStack().isEmpty() && getItemStack().has(IFAttachments.INFINITY_BACKPACK_ID)) {
            BackpackDataManager.BackpackItemHandler handler = BackpackDataManager.CLIENT_SIDE_BACKPACKS.getOrDefault(getItemStack().get(IFAttachments.INFINITY_BACKPACK_ID), null);
            AssetUtil.drawAsset(guiGraphics, screen, provider.getAsset(AssetTypes.SLOT), guiX + getPosX() - 1, guiY + getPosY() - 1);
            if (handler != null) {
                BackpackDataManager.SlotDefinition display = handler.getSlotDefinition(slotId);
                if (!display.getStack().isEmpty()) {
                    guiGraphics.renderItem(display.getStack(), guiX + getPosX(), guiY + getPosY());
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 260);
                    guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                    String amount = NumberUtils.getFormatedBigNumber(display.getAmount());
                    guiGraphics.drawString(Minecraft.getInstance().font, amount, (int) ((guiX + getPosX() + 17 - Minecraft.getInstance().font.width(amount) / 2f) * 2), (guiY + getPosY() + 13) * 2, 0xFFFFFF, true);
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX - guiX, mouseY - guiY)) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 256);
            AssetUtil.drawSelectingOverlay(guiGraphics, getPosX(), getPosY(), getPosX() + getXSize() - 1, getPosY() + getYSize() - 1);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //Minecraft.getInstance().getSoundHandler().play(new SimpleSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f, Minecraft.getInstance().player.getPosition())); //getPosition
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AbstractContainerScreen && ((AbstractContainerScreen) screen).getMenu() instanceof ILocatable) {
            if (!isMouseOver(mouseX - ((AbstractContainerScreen<?>) screen).getGuiLeft(), mouseY - ((AbstractContainerScreen<?>) screen).getGuiTop()))
                return false;
            ILocatable locatable = (ILocatable) ((AbstractContainerScreen) screen).getMenu();
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.putString("Id", getItemStack().get(IFAttachments.INFINITY_BACKPACK_ID));
            compoundNBT.putInt("Slot", slotId);
            compoundNBT.putInt("Button", button);
            compoundNBT.putBoolean("Shift", Screen.hasShiftDown());
            compoundNBT.putBoolean("Ctrl", Screen.hasControlDown());
            Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), 4, compoundNBT));
            return true;
        }
        return false;
    }

    public abstract ItemStack getItemStack();

    @Override
    public List<Component> getTooltipLines() {
        List<Component> lines = new ArrayList<>();
        BackpackDataManager.BackpackItemHandler handler = BackpackDataManager.CLIENT_SIDE_BACKPACKS.getOrDefault(getItemStack().get(IFAttachments.INFINITY_BACKPACK_ID), null);
        if (handler != null) {
            BackpackDataManager.SlotDefinition display = handler.getSlotDefinition(slotId);
            if (!display.getStack().isEmpty()) {
                lines.addAll(Minecraft.getInstance().screen.getTooltipFromItem(Minecraft.getInstance(), display.getStack()));
                lines.add(Component.literal(ChatFormatting.GOLD + new DecimalFormat().format(display.getAmount())));
            }
            String changeVoid = ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.tooltip.ctrl_left").getString();
            if (display.isVoidItems()) {
                lines.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.backpack.void").getString() + ChatFormatting.GREEN + Component.translatable("text.industrialforegoing.display.enabled").getString() + " " + changeVoid));
            } else {
                lines.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.backpack.void").getString() + ChatFormatting.RED + Component.translatable("text.industrialforegoing.display.disabled").getString() + " " + changeVoid));
            }
            String changeRefill = ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.tooltip.ctrl_right").getString();
            if (display.isRefillItems()) {
                lines.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.backpack.refill").getString() + ChatFormatting.GREEN + Component.translatable("text.industrialforegoing.display.enabled").getString() + " " + changeRefill));
            } else {
                lines.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.industrialforegoing.backpack.refill").getString() + ChatFormatting.RED + Component.translatable("text.industrialforegoing.display.disabled").getString() + " " + changeRefill));
            }
        }
        return lines;
    }
}
