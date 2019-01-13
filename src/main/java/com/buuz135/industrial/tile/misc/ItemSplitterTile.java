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
import com.buuz135.industrial.proxy.client.infopiece.IHasDisplayString;
import com.buuz135.industrial.proxy.client.infopiece.TextInfoPiece;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class ItemSplitterTile extends CustomSidedTileEntity implements IHasDisplayString {

    private IItemHandlerModifiable input;
    private IItemHandlerModifiable fakeOut;

    private int tick;
    private int size;

    public ItemSplitterTile() {
        super(ItemSplitterTile.class.getName().hashCode());
        input = this.addSimpleInventory(3, "input", EnumDyeColor.BLUE, "input items", new BoundingRectangle(18, 25, 18, 18 * 3), (stack, integer) -> true, (stack, integer) -> false, true, 0);
        fakeOut = this.addSimpleInventory(0, "out", EnumDyeColor.ORANGE, "output items", new BoundingRectangle(30, 90, 0, 0), (stack, integer) -> false, (stack, integer) -> false, false, 0);
        tick = 1;
        size = 1;
        registerSyncIntPart("size", nbtTagInt -> size = nbtTagInt.getInt(), () -> new NBTTagInt(size), SyncProviderLevel.GUI);
    }

    @Override
    protected void innerUpdate() {
        if (this.world.isRemote) return;
        if (++tick <= 4) return;
        for (EnumFacing facing : this.getSideConfig().getSidesForColor(EnumDyeColor.ORANGE)) {
            BlockPos side = this.pos.offset(facing);
            if (this.world.getTileEntity(side) != null && this.world.getTileEntity(side).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                TileEntity tileEntity = this.world.getTileEntity(side);
                IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                for (int i = 0; i < handler.getSlots(); ++i) {
                    if (!handler.getStackInSlot(i).isEmpty() && (handler.getStackInSlot(i).getCount() >= size || handler.getStackInSlot(i).getCount() >= handler.getStackInSlot(i).getMaxStackSize()))
                        continue;
                    ItemStack handlerStack = handler.getStackInSlot(i);
                    ItemStack posible = ItemStack.EMPTY;
                    boolean hasWorked = false;
                    for (int x = 0; x < input.getSlots(); ++x) {
                        if (!input.getStackInSlot(x).isEmpty() && (handlerStack.isEmpty() || input.getStackInSlot(x).isItemEqual(handlerStack))) {
                            posible = input.getStackInSlot(x);
                            if (!posible.isEmpty()) {
                                ItemStack def = posible.copy();
                                def.setCount(1);
                                def = handler.insertItem(i, def, false);
                                if (def.isEmpty()) posible.shrink(1);
                                else continue;
                                hasWorked = true;
                                break;
                            }
                        }
                    }
                    if (hasWorked) break;
                }
            }
        }
        tick = 0;
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new LockedInventoryTogglePiece(18 * 2 + 9, 83, this, EnumDyeColor.BLUE));
        pieces.add(new ArrowInfoPiece(50, 28, 17, 56, "text.industrialforegoing.button.decrease_stack") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    ItemSplitterTile.this.sendToServer(ItemSplitterTile.this.setupSpecialNBTMessage("STACK_DECREASE"));
                }
            }
        });
        pieces.add(new ArrowInfoPiece(156, 28, 33, 56, "text.industrialforegoing.button.increase_stack") {
            @Override
            protected void clicked() {
                if (TeslaCoreLib.INSTANCE.isClientSide()) {
                    ItemSplitterTile.this.sendToServer(ItemSplitterTile.this.setupSpecialNBTMessage("STACK_INCREASE"));
                }
            }
        });
        pieces.add(0, new TextInfoPiece(this, 0, 75, 31));
        return pieces;
    }

    @Nullable
    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        super.processClientMessage(messageType, compound);
        if (messageType.equals("STACK_INCREASE")) {
            size = Math.min(size + 1, 64);
            partialSync("size", true);
        }
        if (messageType.equals("STACK_DECREASE")) {
            size = Math.max(size - 1, 1);
            partialSync("size", true);
        }
        return null;
    }

    private ItemStack getStack(ItemStack stack) {
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty() && (stack.isEmpty() || input.getStackInSlot(i).isItemEqual(stack))) {
                return input.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        tick = compound.getInteger("Tick");
        size = compound.getInteger("Size");
        super.readFromNBT(compound);
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger("Tick", tick);
        tagCompound.setInteger("Stack", size);
        return tagCompound;
    }

    @Override
    public String getString(int id) {
        return TextFormatting.DARK_GRAY + new TextComponentTranslation("text.industrialforegoing.display.stacksize").getFormattedText() + " " + TextFormatting.DARK_GRAY + size;
    }
}
