/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.client.infopiece.PetrifiedFuelInfoPiece;
import com.buuz135.industrial.tile.CustomGeneratorMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class PetrifiedFuelGeneratorTile extends CustomGeneratorMachine {

    private ItemStackHandler inStackHandler;
    private ItemStack current = ItemStack.EMPTY;
    private int burnTime = 0;

    public PetrifiedFuelGeneratorTile() {
        super(PetrifiedFuelGeneratorTile.class.getName().hashCode());

    }

    public static long getEnergyProduced(int burnTime) {
        return burnTime / BlockRegistry.petrifiedFuelGeneratorBlock.getTimeModifier();
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        this.inStackHandler = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                PetrifiedFuelGeneratorTile.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.inStackHandler, EnumDyeColor.GREEN, "Fuel input", new BoundingRectangle(61, 25, 18, 54)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return PetrifiedFuelGeneratorTile.this.acceptsInputStack(slot, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18, 1, 3,
                        BasicTeslaGuiContainer.Companion.getMACHINE_BACKGROUND(), 108, 225, EnumDyeColor.GREEN));
                return pieces;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle boundingRectangle = this.getBoundingBox();
                for (int y = 0; y < this.getInnerHandler().getSlots(); y++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), y, boundingRectangle.getLeft() + 1, boundingRectangle.getTop() + 1 + y * 18));
                }
                return slots;
            }
        });
        super.addInventoryToStorage(this.inStackHandler, "pet_gen_input");

    }

    private boolean acceptsInputStack(int slot, ItemStack stack) {
        return !stack.isEmpty() && TileEntityFurnace.isItemFuel(stack) && !stack.getItem().equals(Items.LAVA_BUCKET) && !stack.getItem().equals(ForgeModContainer.getInstance().universalBucket) && getEnergyProduced(TileEntityFurnace.getItemBurnTime(stack)) > 0;
    }

    public ItemStack getFirstFuel(boolean replace) {
        if (!replace) return this.current;
        for (int i = 0; i < inStackHandler.getSlots(); ++i) {
            if (!inStackHandler.getStackInSlot(i).isEmpty()) {
                this.current = new ItemStack(inStackHandler.getStackInSlot(i).getItem(), 1);
                this.forceSync();
                return inStackHandler.getStackInSlot(i);
            }
        }
        return current = ItemStack.EMPTY;
    }

    @Override
    public long consumeFuel() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        ItemStack temp = this.getFirstFuel(true);
        if (temp.isEmpty()) {
            return 0;
        }
        burnTime = TileEntityFurnace.getItemBurnTime(temp);
        temp.setCount(temp.getCount() - 1);
        return (long) (burnTime * 100 * BlockRegistry.petrifiedFuelGeneratorBlock.getBurnTimeMultiplier());
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = super.getGuiContainerPieces(container);
        list.add(new PetrifiedFuelInfoPiece(this, 88, 25));
        return list;
    }

    @Override
    protected long getEnergyFillRate() {
        return getEnergyProduced(burnTime);
    }

    public ItemStack getCurrent() {
        return current;
    }


}
