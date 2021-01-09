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
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        if (!getItemStack().isEmpty() && getItemStack().hasTag()){
            BackpackDataManager.BackpackItemHandler handler = BackpackDataManager.CLIENT_SIDE_BACKPACKS.getOrDefault(getItemStack().getTag().getString("Id"), null);
            AssetUtil.drawAsset(stack, screen, provider.getAsset(AssetTypes.SLOT), guiX + getPosX(), guiY + getPosY());
            if (handler != null){
                BackpackDataManager.SlotDefinition display = handler.getSlotDefinition(slotId);
                if (!display.getStack().isEmpty()){
                    Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(display.getStack(), guiX + getPosX() + 1, guiY + getPosY() + 1);
                    stack.push();
                    stack.translate(0,0, 260);
                    stack.scale(0.5f, 0.5f, 0.5f);
                    String amount = NumberUtils.getFormatedBigNumber(display.getAmount());
                    Minecraft.getInstance().fontRenderer.drawStringWithShadow(stack, amount , (guiX + getPosX() + 17 - Minecraft.getInstance().fontRenderer.getStringWidth(amount) / 2f)*2, (guiY + getPosY() + 13)*2, 0xFFFFFF);
                    stack.pop();
                }
            }
        }
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {
        if (isInside(screen, mouseX - guiX, mouseY - guiY)) {
            stack.push();
            stack.translate(0,0,256);
            AssetUtil.drawSelectingOverlay(stack, getPosX() + 1, getPosY() + 1, getPosX() + getXSize() - 1, getPosY() + getYSize() - 1);
            stack.pop();
        }
    }

    @Override
    public void handleClick(Screen screen, int guiX, int guiY, double mouseX, double mouseY, int button) {
        //Minecraft.getInstance().getSoundHandler().play(new SimpleSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f, Minecraft.getInstance().player.getPosition())); //getPosition
        if (screen instanceof ContainerScreen && ((ContainerScreen) screen).getContainer() instanceof ILocatable) {
            ILocatable locatable = (ILocatable) ((ContainerScreen) screen).getContainer();
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString("Id", getItemStack().getTag().getString("Id"));
            compoundNBT.putInt("Slot", slotId);
            compoundNBT.putInt("Button", button);
            compoundNBT.putBoolean("Shift", Screen.hasShiftDown());
            compoundNBT.putBoolean("Ctrl", Screen.hasControlDown());
            Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), 4, compoundNBT));
        }
    }

    public abstract ItemStack getItemStack();

    @Override
    public List<ITextComponent> getTooltipLines() {
        List<ITextComponent> lines = new ArrayList<>();
        BackpackDataManager.BackpackItemHandler handler = BackpackDataManager.CLIENT_SIDE_BACKPACKS.getOrDefault(getItemStack().getTag().getString("Id"), null);
        if (handler != null) {
            BackpackDataManager.SlotDefinition display = handler.getSlotDefinition(slotId);
            if (!display.getStack().isEmpty()) {
                lines.addAll(Minecraft.getInstance().currentScreen.getTooltipFromItem(display.getStack()));
                lines.add(new StringTextComponent(TextFormatting.GOLD + new DecimalFormat().format(display.getAmount())));
            }
            String changeVoid = TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.tooltip.ctrl_left").getString();
            if (display.isVoidItems()){
                lines.add(new StringTextComponent(TextFormatting.GOLD +new TranslationTextComponent("tooltip.industrialforegoing.backpack.void").getString() + TextFormatting.GREEN + new TranslationTextComponent("text.industrialforegoing.display.enabled").getString() + " " + changeVoid));
            } else {
                lines.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.backpack.void").getString() +TextFormatting.RED + new TranslationTextComponent("text.industrialforegoing.display.disabled").getString() + " "  + changeVoid));
            }
            String changeRefill = TextFormatting.DARK_GRAY + new TranslationTextComponent("text.industrialforegoing.tooltip.ctrl_right").getString();
            if (display.isRefillItems()){
                lines.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.backpack.refill").getString() + TextFormatting.GREEN + new TranslationTextComponent("text.industrialforegoing.display.enabled").getString() + " " + changeRefill));
            } else {
                lines.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.backpack.refill").getString()+ TextFormatting.RED + new TranslationTextComponent("text.industrialforegoing.display.disabled").getString() + " " + changeRefill));
            }
        }
        return lines;
    }
}
