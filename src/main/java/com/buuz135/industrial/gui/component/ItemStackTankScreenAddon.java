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

import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.interfaces.IClickable;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemStackTankScreenAddon extends BasicScreenAddon implements IClickable {

    private IFluidHandler tank;
    private ITankAsset asset;
    private FluidTankComponent.Type type;
    private int tankSlot;

    public ItemStackTankScreenAddon(int posX, int posY, IFluidHandler tank, int tankSlot, FluidTankComponent.Type type) {
        super(posX, posY);
        this.tank = tank;
        this.type = type;
        this.tankSlot = tankSlot;
    }

    @Override
    public void drawBackgroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        asset = IAssetProvider.getAsset(provider, type.getAssetType());
        Rectangle area = asset.getArea();
        if (!tank.getFluidInTank(tankSlot).isEmpty()) {
            FluidStack fluidStack = tank.getFluidInTank(tankSlot);
            int stored = fluidStack.getAmount();
            int capacity = tank.getTankCapacity(tankSlot);
            int topBottomPadding = asset.getFluidRenderPadding(Direction.UP) + asset.getFluidRenderPadding(Direction.DOWN);
            int offset = (stored * (area.height - topBottomPadding) / capacity);
            ResourceLocation flowing = fluidStack.getFluid().getAttributes().getStillTexture(fluidStack);
            if (flowing != null) {
                Texture texture = screen.getMinecraft().getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE); //getAtlasSprite
                if (texture instanceof AtlasTexture) {
                    TextureAtlasSprite sprite = ((AtlasTexture) texture).getSprite(flowing);
                    if (sprite != null) {
                        screen.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                        Color color = new Color(fluidStack.getFluid().getAttributes().getColor());
                        RenderSystem.color4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
                        RenderSystem.enableBlend();
                        Screen.blit(stack, this.getPosX() + guiX + asset.getFluidRenderPadding(Direction.WEST),
                            this.getPosY() + guiY + asset.getFluidRenderPadding(Direction.UP) + (fluidStack.getFluid().getAttributes().isGaseous() ? 0 : (area.height - topBottomPadding) - offset),
                            0,
                            (int) (area.getWidth() - asset.getFluidRenderPadding(Direction.EAST) - asset.getFluidRenderPadding(Direction.WEST)),
                            offset,
                            sprite);
                        RenderSystem.disableBlend();
                        RenderSystem.color4f(1, 1, 1, 1);
                    }
                }
            }
        }
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.enableAlphaTest();
        ITankAsset asset = IAssetProvider.getAsset(provider, type.getAssetType());
        AssetUtil.drawAsset(stack, screen, asset, guiX + getPosX(), guiY + getPosY());
    }

    @Override
    public void drawForegroundLayer(MatrixStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {}

    @Override
    public List<ITextComponent> getTooltipLines() {
        List<ITextComponent> strings = new ArrayList<>();
        strings.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.titanium.tank.fluid").getString()).append(tank.getFluidInTank(tankSlot).isEmpty() ? new TranslationTextComponent("tooltip.titanium.tank.empty").mergeStyle(TextFormatting.WHITE) :  new TranslationTextComponent(tank.getFluidInTank(tankSlot).getFluid().getAttributes().getTranslationKey(tank.getFluidInTank(tankSlot)))).mergeStyle(TextFormatting.WHITE));
        strings.add(new TranslationTextComponent("tooltip.titanium.tank.amount").mergeStyle(TextFormatting.GOLD).append(new StringTextComponent(TextFormatting.WHITE + new DecimalFormat().format(tank.getFluidInTank(tankSlot).getAmount()) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(tank.getTankCapacity(tankSlot)) + TextFormatting.DARK_AQUA + "mb")));
        if (!Minecraft.getInstance().player.inventory.getItemStack().isEmpty() && Minecraft.getInstance().player.inventory.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            Minecraft.getInstance().player.inventory.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
                boolean canFillFromItem = tank.fill(iFluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                boolean canDrainFromItem = iFluidHandlerItem.fill(tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                if (canFillFromItem) strings.add(new TranslationTextComponent("tooltip.titanium.tank.can_fill_from_item").mergeStyle(TextFormatting.BLUE));
                if (canDrainFromItem) strings.add(new TranslationTextComponent("tooltip.titanium.tank.can_drain_from_item").mergeStyle(TextFormatting.GOLD));
                if (canFillFromItem) strings.add(new TranslationTextComponent("tooltip.titanium.tank.action_fill").mergeStyle(TextFormatting.DARK_GRAY));
                if (canDrainFromItem) strings.add(new TranslationTextComponent("tooltip.titanium.tank.action_drain").mergeStyle(TextFormatting.DARK_GRAY));
                if (!canDrainFromItem && !canFillFromItem){
                    strings.add(new TranslationTextComponent("tooltip.titanium.tank.no_action").mergeStyle(TextFormatting.RED));
                }
            });
        } else {
            strings.add(new TranslationTextComponent("tooltip.titanium.tank.no_tank").mergeStyle(TextFormatting.DARK_GRAY));
        }
        return strings;
    }

    @Override
    public int getXSize() {
        return asset != null ? asset.getArea().width : 0;
    }

    @Override
    public int getYSize() {
        return asset != null ? asset.getArea().height : 0;
    }

    @Override
    public void handleClick(Screen screen, int guiX, int guiY, double mouseX, double mouseY, int button) {
        if (!Minecraft.getInstance().player.inventory.getItemStack().isEmpty() && Minecraft.getInstance().player.inventory.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            Minecraft.getInstance().getSoundHandler().play(new SimpleSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1f, 1f, Minecraft.getInstance().player.getPosition())); //getPosition
            if (screen instanceof ContainerScreen && ((ContainerScreen) screen).getContainer() instanceof ILocatable) {
                ILocatable locatable = (ILocatable) ((ContainerScreen) screen).getContainer();
                CompoundNBT compoundNBT = new CompoundNBT();
                if (tank instanceof FluidTankComponent){
                    compoundNBT.putString("Name",((FluidTankComponent<?>) tank).getName());
                } else {
                    compoundNBT.putBoolean("Invalid", true);
                }
                Minecraft.getInstance().player.inventory.getItemStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
                    boolean canFillFromItem = tank.fill(iFluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                    boolean canDrainFromItem = iFluidHandlerItem.fill(tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                    if (canFillFromItem && button == 0) compoundNBT.putBoolean("Fill", true);
                    if (canDrainFromItem && button == 1) compoundNBT.putBoolean("Fill", false);
                });
                Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), -3, compoundNBT));
            }
        }
    }
}
