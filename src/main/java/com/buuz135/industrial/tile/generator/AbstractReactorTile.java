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
package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.buuz135.industrial.proxy.client.infopiece.BioreactorEfficiencyInfoPiece;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReactorTile extends CustomElectricMachine {

    private LockableItemHandler input;
    private IFluidTank tank;

    public AbstractReactorTile(int i) {
        super(i);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(getProducedFluid(), 8000, EnumDyeColor.PURPLE, "output tank", new BoundingRectangle(48, 25, 18, 54));
        input = new LockableItemHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                AbstractReactorTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 16;
            }
        };
        this.addInventory(new ColoredItemHandler(input, EnumDyeColor.BLUE, "Input items", new BoundingRectangle(18 * 5, 25, 3 * 18, 3 * 18)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return super.canInsertItem(slot, stack) && canInsert(slot, input, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(input, "input");
    }


    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BioreactorEfficiencyInfoPiece(this, 149, 25));
        pieces.add(1, new LockedInventoryTogglePiece(18 * 7 + 9, 83, this, EnumDyeColor.BLUE));
        return pieces;
    }


    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        if (getEfficiency() < 0) return 0;
        FluidStack stack = new FluidStack(getProducedFluid(), getProducedAmountItem() * getItemAmount());
        if (tank.getFluid() == null || (stack.amount + tank.getFluidAmount() <= tank.getCapacity())) {
            tank.fill(stack, true);
            List<ItemStack> used = new ArrayList<>();
            for (int i = 0; i < input.getSlots(); ++i) {
                int finalI = i;
                if (!input.getStackInSlot(i).isEmpty() && used.stream().noneMatch((stack1 -> stack1.isItemEqual(input.getStackInSlot(finalI))))) {
                    used.add(input.getStackInSlot(i).copy());
                    input.getStackInSlot(i).setCount(input.getStackInSlot(i).getCount() - 1);
                }

            }
            return 1;
        }
        return 0;
    }

    private boolean canInsert(int slot, ItemStackHandler handler, ItemStack stack) {
        if (getReactorsEntries().stream().noneMatch(entry -> entry.doesStackMatch(stack))) {
            return false;
        }
        if (handler.getStackInSlot(slot).isItemEqual(stack)) {
            return true;
        }
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (stack.isItemEqual(handler.getStackInSlot(i)))
                return false;
        }
        return true;
    }


    public int getItemAmount() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < input.getSlots(); ++i) {
            if (!input.getStackInSlot(i).isEmpty()) {
                boolean isItNew = true;
                for (ItemStack stack : stacks) {
                    if (stack.isItemEqual(input.getStackInSlot(i))) {
                        isItNew = false;
                        break;
                    }
                }
                if (isItNew) stacks.add(input.getStackInSlot(i));
            }
        }
        return stacks.size();
    }

    public float getEfficiency() {
        return (getItemAmount() - 1) / 8f;
    }

    public int getProducedAmountItem() {
        float eff = getEfficiency();
        if (eff < 0) return 0;
        int base = amountProduced();
        return (int) (getEfficiency() * base + base);
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.fillItemFromTank(fluidItems, tank);
    }

    public abstract List<IReactorEntry> getReactorsEntries();

    public abstract Fluid getProducedFluid();

    public abstract int amountProduced();
}
