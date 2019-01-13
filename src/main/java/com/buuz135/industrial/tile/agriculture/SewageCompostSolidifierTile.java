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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class SewageCompostSolidifierTile extends CustomElectricMachine {

    private IFluidTank sewage;
    private ItemStackHandler outFertilizer;

    public SewageCompostSolidifierTile() {
        super(SewageCompostSolidifierTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        sewage = this.addFluidTank(FluidsRegistry.SEWAGE, 8000, EnumDyeColor.BROWN, "Sewage tank", new BoundingRectangle(50, 25, 18, 54));
        outFertilizer = new ItemStackHandler(4 * 3) {
            @Override
            protected void onContentsChanged(int slot) {
                SewageCompostSolidifierTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outFertilizer, EnumDyeColor.ORANGE, "Output Items", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outFertilizer, "outFertilizer");
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        ItemStack stack = new ItemStack(ItemRegistry.fertilizer, 1);
        if (sewage.getFluid() != null && sewage.drain(2000, false).amount == 2000 && ItemHandlerHelper.insertItem(outFertilizer, stack, true).isEmpty()) {
            sewage.drain(2000, true);
            ItemHandlerHelper.insertItem(outFertilizer, stack, false);
            return 1;
        }

        return 0;
    }
}
