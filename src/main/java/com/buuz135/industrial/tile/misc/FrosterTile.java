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
package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.client.infopiece.ArrowInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class FrosterTile extends CustomElectricMachine {

    private IFluidTank tank;
    private FrosterResult result;
    private ItemStackHandler outItems;

    public FrosterTile() {
        super(FrosterTile.class.getName().hashCode());
        result = FrosterResult.NONE;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(8000, "water", EnumDyeColor.BLUE, 48, 25, FluidTankType.INPUT, stack -> stack.getFluid().equals(FluidRegistry.WATER), stack -> false);
        outItems = new ItemStackHandler(3 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                FrosterTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output Items", 18 * 6 + 3, 25, 3, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outItems, "outItems");
        this.registerSyncStringPart("result", nbtTagString -> result = FrosterResult.valueOf(nbtTagString.getString()), () -> new NBTTagString(result.name()), SyncProviderLevel.GUI);
    }

    @Override
    public @NotNull List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(88, 43, 18, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png"), 1, 168) {
            @Override
            public void drawBackgroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
                super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY);
                if (result == FrosterResult.NONE) {
                    container.mc.getTextureManager().bindTexture(new ResourceLocation("teslacorelib", "textures/gui/basic-machine.png"));
                    container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 146, 210, 14, 14);
                } else {
                    RenderHelper.enableGUIStandardItemLighting();
                    container.mc.getRenderItem().renderItemIntoGUI(result.getResult(), guiX + this.getLeft() + 1, guiY + this.getTop() + 1);
                }
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
                if (!isInside(container, mouseX, mouseY)) return;
                if (result == FrosterResult.NONE) {
                    container.drawTooltip(Arrays.asList(new TextComponentTranslation("text.industrialforegoing.button.none").getUnformattedComponentText()), mouseX - guiX, mouseY - guiY);
                } else {
                    List<String> tooltip = container.getItemToolTip(result.getResult());
                    tooltip.add(1, TextFormatting.GRAY + "" + result.fluid + "mb");
                    container.drawTooltip(tooltip, mouseX - guiX, mouseY - guiY);
                }
            }
        });
        pieces.add(new ArrowInfoPiece(90, 26, 17, 72, "text.industrialforegoing.button.decrease") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    FrosterTile.this.sendToServer(FrosterTile.this.setupSpecialNBTMessage("PREV_OUTPUT"));
                }
            }
        });
        pieces.add(new ArrowInfoPiece(90, 65, 33, 72, "text.industrialforegoing.button.increase") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    FrosterTile.this.sendToServer(FrosterTile.this.setupSpecialNBTMessage("NEXT_OUTPUT"));
                }
            }
        });
        return pieces;
    }

    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(@Nullable String messageType, @NotNull NBTTagCompound compound) {
        if (messageType == null) return super.processClientMessage(messageType, compound);
        if (messageType.equals("NEXT_OUTPUT")) {
            result = FrosterResult.values()[(Arrays.asList(FrosterResult.values()).indexOf(result) + 1) % FrosterResult.values().length];
            partialSync("result", true);
        }
        if (messageType.equals("PREV_OUTPUT")) {
            int pointer = (Arrays.asList(FrosterResult.values()).indexOf(result) - 1);
            if (pointer < 0) pointer = FrosterResult.values().length - 1;
            result = FrosterResult.values()[pointer];
            partialSync("result", true);
        }
        return super.processClientMessage(messageType, compound);
    }

    @Override
    protected float performWork() {
        if (result != FrosterResult.NONE && tank.getFluidAmount() >= result.getFluid() && ItemHandlerHelper.insertItem(outItems, result.getResult(), true).isEmpty()) {
            tank.drain(result.getFluid(), true);
            ItemHandlerHelper.insertItem(outItems, result.getResult().copy(), false);
            return 1;
        }
        return 0;
    }

    public enum FrosterResult {
        NONE(ItemStack.EMPTY, 1),
        SNOW(new ItemStack(Items.SNOWBALL), 250),
        ICE(new ItemStack(Blocks.ICE), 1000),
        PACKED(new ItemStack(Blocks.PACKED_ICE), 2500);

        private final ItemStack result;
        private final int fluid;

        FrosterResult(ItemStack result, int fluid) {
            this.result = result;
            this.fluid = fluid;
        }

        public ItemStack getResult() {
            return result;
        }

        public int getFluid() {
            return fluid;
        }
    }
}
