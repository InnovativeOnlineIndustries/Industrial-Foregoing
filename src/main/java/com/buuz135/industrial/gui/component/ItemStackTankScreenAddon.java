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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.BucketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fluids.FluidAttributes;
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
    public void drawBackgroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
//        asset = IAssetProvider.getAsset(provider, type.getAssetType());
//        Rectangle area = asset.getArea();
//        if (!tank.getFluidInTank(tankSlot).isEmpty()) {
//            FluidStack fluidStack = tank.getFluidInTank(tankSlot);
//            int stored = fluidStack.getAmount();
//            int capacity = tank.getTankCapacity(tankSlot);
//            int topBottomPadding = asset.getFluidRenderPadding(Direction.UP) + asset.getFluidRenderPadding(Direction.DOWN);
//            int offset = (stored * (area.height - topBottomPadding) / capacity);
//            ResourceLocation flowing = fluidStack.getFluid().getAttributes().getStillTexture(fluidStack);
//            if (flowing != null) {
//                AbstractTexture texture = screen.getMinecraft().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS); //getAtlasSprite
//                if (texture instanceof TextureAtlas) {
//                    TextureAtlasSprite sprite = ((TextureAtlas) texture).getSprite(flowing);
//                    if (sprite != null) {
//                        screen.getMinecraft().getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
//                        Color color = new Color(fluidStack.getFluid().getAttributes().getColor());
//                        RenderSystem.color4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
//                        RenderSystem.enableBlend();
//                        Screen.blit(stack, this.getPosX() + guiX + asset.getFluidRenderPadding(Direction.WEST),
//                            this.getPosY() + guiY + asset.getFluidRenderPadding(Direction.UP) + (fluidStack.getFluid().getAttributes().isGaseous() ? 0 : (area.height - topBottomPadding) - offset),
//                            0,
//                            (int) (area.getWidth() - asset.getFluidRenderPadding(Direction.EAST) - asset.getFluidRenderPadding(Direction.WEST)),
//                            offset,
//                            sprite);
//                        RenderSystem.disableBlend();
//                        RenderSystem.color4f(1, 1, 1, 1);
//                    }
//                }
//            }
//        }
//        RenderSystem.color4f(1, 1, 1, 1);
//        RenderSystem.enableAlphaTest();
//        ITankAsset asset = IAssetProvider.getAsset(provider, type.getAssetType());
//        AssetUtil.drawAsset(stack, screen, asset, guiX + getPosX(), guiY + getPosY());
    }

    @Override
    public void drawForegroundLayer(PoseStack stack, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY) {}

    @Override
    public List<Component> getTooltipLines() {
        List<Component> strings = new ArrayList<>();
        strings.add(new TextComponent(ChatFormatting.GOLD + new TranslatableComponent("tooltip.titanium.tank.fluid").getString()).append(tank.getFluidInTank(tankSlot).isEmpty() ? new TranslatableComponent("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) :  new TranslatableComponent(tank.getFluidInTank(tankSlot).getFluid().getAttributes().getTranslationKey(tank.getFluidInTank(tankSlot)))).withStyle(ChatFormatting.WHITE));
        strings.add(new TranslatableComponent("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(new TextComponent(ChatFormatting.WHITE + new DecimalFormat().format(tank.getFluidInTank(tankSlot).getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(tank.getTankCapacity(tankSlot)) + ChatFormatting.DARK_AQUA + "mb")));
        if (!Minecraft.getInstance().player.inventory.getSelected().isEmpty() && Minecraft.getInstance().player.inventory.getSelected().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            Minecraft.getInstance().player.inventory.getSelected().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
                boolean isBucket = Minecraft.getInstance().player.inventory.getSelected().getItem() instanceof BucketItem;
                int amount = isBucket ? FluidAttributes.BUCKET_VOLUME : Integer.MAX_VALUE;
                boolean canFillFromItem = false;
                boolean canDrainFromItem = false;
                if (isBucket) {
                    canFillFromItem = tank.fill(iFluidHandlerItem.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME;
                    canDrainFromItem = iFluidHandlerItem.fill(tank.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME;
                } else {
                    canFillFromItem = tank.fill(iFluidHandlerItem.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                    canDrainFromItem = iFluidHandlerItem.fill(tank.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                }
                if (canFillFromItem)
                    strings.add(new TranslatableComponent("tooltip.titanium.tank.can_fill_from_item").withStyle(ChatFormatting.BLUE));
                if (canDrainFromItem)
                    strings.add(new TranslatableComponent("tooltip.titanium.tank.can_drain_from_item").withStyle(ChatFormatting.GOLD));
                if (canFillFromItem)
                    strings.add(new TranslatableComponent("tooltip.titanium.tank.action_fill").withStyle(ChatFormatting.DARK_GRAY));
                if (canDrainFromItem)
                    strings.add(new TranslatableComponent("tooltip.titanium.tank.action_drain").withStyle(ChatFormatting.DARK_GRAY));
                if (!canDrainFromItem && !canFillFromItem) {
                    strings.add(new TranslatableComponent("tooltip.titanium.tank.no_action").withStyle(ChatFormatting.RED));
                }
            });
        } else {
            strings.add(new TranslatableComponent("tooltip.titanium.tank.no_tank").withStyle(ChatFormatting.DARK_GRAY));
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
        if (!Minecraft.getInstance().player.inventory.getSelected().isEmpty() && Minecraft.getInstance().player.inventory.getSelected().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 1f, 1f, Minecraft.getInstance().player.blockPosition())); //getPosition
            if (screen instanceof AbstractContainerScreen && ((AbstractContainerScreen) screen).getMenu() instanceof ILocatable) {
                ILocatable locatable = (ILocatable) ((AbstractContainerScreen) screen).getMenu();
                CompoundTag compoundNBT = new CompoundTag();
                if (tank instanceof FluidTankComponent){
                    compoundNBT.putString("Name",((FluidTankComponent<?>) tank).getName());
                } else {
                    compoundNBT.putBoolean("Invalid", true);
                }
                Minecraft.getInstance().player.inventory.getSelected().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(iFluidHandlerItem -> {
                    boolean isBucket = Minecraft.getInstance().player.inventory.getSelected().getItem() instanceof BucketItem;
                    int amount = isBucket ? FluidAttributes.BUCKET_VOLUME : Integer.MAX_VALUE;
                    boolean canFillFromItem = false;
                    boolean canDrainFromItem = false;
                    if (isBucket) {
                        canFillFromItem = tank.fill(iFluidHandlerItem.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME;
                        canDrainFromItem = iFluidHandlerItem.fill(tank.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME;
                    } else {
                        canFillFromItem = tank.fill(iFluidHandlerItem.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                        canDrainFromItem = iFluidHandlerItem.fill(tank.drain(amount, IFluidHandler.FluidAction.SIMULATE), IFluidHandler.FluidAction.SIMULATE) > 0;
                    }
                    if (canFillFromItem && button == 0) compoundNBT.putBoolean("Fill", true);
                    if (canDrainFromItem && button == 1) compoundNBT.putBoolean("Fill", false);
                });
                Titanium.NETWORK.get().sendToServer(new ButtonClickNetworkMessage(locatable.getLocatorInstance(), -3, compoundNBT));
            }
        }
    }
}
