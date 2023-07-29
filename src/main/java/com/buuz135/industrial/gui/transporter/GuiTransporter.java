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

package com.buuz135.industrial.gui.transporter;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.api.transporter.TransporterType;
import com.buuz135.industrial.gui.component.custom.FilterGuiComponent;
import com.buuz135.industrial.gui.component.custom.ICanSendNetworkMessage;
import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.network.TransporterButtonInteractMessage;
import com.buuz135.industrial.utils.Reference;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiTransporter extends AbstractContainerScreen<ContainerTransporter> implements ICanSendNetworkMessage {

    public static final ResourceLocation BG_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/conveyor.png");

    private TransporterType upgrade;
    private List<IGuiComponent> componentList;
    private int x;
    private int y;
    private List<IFilter.GhostSlot> ghostSlots;

    public GuiTransporter(ContainerTransporter inventorySlotsIn, Inventory inventory, Component component) {
        super(inventorySlotsIn, inventory, component);
        this.upgrade = getMenu().getConveyor().getTransporterTypeMap().get(getMenu().getFacing());
        this.componentList = new ArrayList<>();
        this.ghostSlots = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        componentList.clear();
        upgrade.addComponentsToGui(componentList);
        for (IGuiComponent iGuiComponent : componentList) {
            if (iGuiComponent instanceof FilterGuiComponent) {
                ghostSlots.addAll(Arrays.asList(((FilterGuiComponent) iGuiComponent).getFilter().getFilter()));
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) { //background
        this.renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //RenderSystem.setShaderTexture(0, BG_TEXTURE);
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if (upgrade != null) {
            String localized = Component.translatable(upgrade.getFactory().getUpgradeItem().getDescriptionId()).getString();
            guiGraphics.drawString(Minecraft.getInstance().font, localized, x + imageWidth / 2 - getMinecraft().font.width(localized) / 2, y + 6, 0x404040);
        }
        for (IGuiComponent iGuiComponent : componentList) {
            iGuiComponent.drawGuiBackgroundLayer(guiGraphics, x, y, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) { //foreground
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;
        for (IGuiComponent iGuiComponent : componentList) {
            iGuiComponent.drawGuiForegroundLayer(guiGraphics, x, y, mouseX, mouseY);
        }
        renderTooltip(guiGraphics, mouseX - x, mouseY - y);
        for (IGuiComponent iGuiComponent : componentList) {
            if (iGuiComponent.isInside(mouseX - x, mouseY - y)) {
                List<Component> tooltips = iGuiComponent.getTooltip(x, y, mouseX, mouseY);
                if (tooltips != null) guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, tooltips, mouseX - x, mouseY - y);
            }
        }
    }

    public ContainerTransporter getMenu() {
        return (ContainerTransporter) super.getMenu();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) { //mouseClicked
        boolean click = super.mouseClicked(mouseX, mouseY, mouseButton);
        for (IGuiComponent iGuiComponent : componentList) {
            if (iGuiComponent.isInside(mouseX - x, mouseY - y)) {
                if (iGuiComponent.handleClick(this, x, y, mouseX, mouseY))
                    return true;
            }
        }
        return click;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean click = super.mouseScrolled(mouseX, mouseY, delta);
        for (IGuiComponent iGuiComponent : componentList) {
            if (iGuiComponent.isInside(mouseX - x, mouseY - y)) {
                if (iGuiComponent.onScrolled(this, x, y, mouseX, mouseY, delta))
                    return true;
            }
        }
        return click;
    }

    @Override
    public void sendMessage(int id, CompoundTag compound) {
        IndustrialForegoing.NETWORK.get().sendToServer(new TransporterButtonInteractMessage(upgrade.getPos(), id, upgrade.getSide(), compound));
    }

    public List<IFilter.GhostSlot> getGhostSlots() {
        return ghostSlots;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
