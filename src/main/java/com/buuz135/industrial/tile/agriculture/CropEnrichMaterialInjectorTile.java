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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class CropEnrichMaterialInjectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";

    private ItemStackHandler inFert;
    private int pointer;


    public CropEnrichMaterialInjectorTile() {
        super(CropEnrichMaterialInjectorTile.class.getName().hashCode());
        pointer = 0;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inFert = new ItemStackHandler(12) {
            @Override
            protected void onContentsChanged(int slot) {
                CropEnrichMaterialInjectorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(inFert, EnumDyeColor.GREEN, "Fertilizer input", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return isValidFertilizer(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        this.addInventoryToStorage(inFert, "inFert");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean needsToIncrease = true;
        if (pointer >= blockPos.size()) pointer = 0;
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            if (!this.world.isAirBlock(pos)) {
                ItemStack stack = getFirstItem();
                if (!stack.isEmpty()) {
                    FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                    if (ItemDye.applyBonemeal(stack, this.world, pos, player, EnumHand.MAIN_HAND))
                        needsToIncrease = false;
                }
            }
        } else {
            pointer = 0;
        }
        if (needsToIncrease) ++pointer;
        return 1;
    }

    private ItemStack getFirstItem() {
        for (int i = 0; i < inFert.getSlots(); ++i)
            if (!inFert.getStackInSlot(i).isEmpty()) return inFert.getStackInSlot(i);
        return ItemStack.EMPTY;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_POINTER)) pointer = 0;
        else pointer = compound.getInteger(NBT_POINTER);
    }

    public boolean isValidFertilizer(ItemStack stack) {
        return stack.getItem().getRegistryName().toString().equals("forestry:fertilizer_compound") || (stack.getItem().equals(Items.DYE) && stack.getMetadata() == 15) || stack.getItem().equals(ItemRegistry.fertilizer) || ItemStackUtils.isStackOreDict(stack, "fertilizer");
    }

}
