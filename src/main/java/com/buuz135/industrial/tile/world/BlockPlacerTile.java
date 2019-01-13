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
package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class BlockPlacerTile extends WorkingAreaElectricMachine {

    private ItemStackHandler inItems;

    public BlockPlacerTile() {
        super(BlockPlacerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inItems = new ItemStackHandler(3 * 6) {
            @Override
            protected void onContentsChanged(int slot) {
                BlockPlacerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(inItems, EnumDyeColor.BLUE, "Input items", 18 * 3, 25, 6, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(inItems, "block_destroyer_out");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea());
        for (BlockPos pos : blockPosList) {
            if (this.world.isAirBlock(pos)) {
                ItemStack stack = getFirstStackHasBlock();
                if (stack.isEmpty()) return 0;
                if (this.world.isAirBlock(pos)) {
                    FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                    player.setHeldItem(EnumHand.MAIN_HAND, stack);
                    EnumActionResult result = ForgeHooks.onPlaceItemIntoWorld(stack, player, world, pos, EnumFacing.UP, 0, 0, 0, EnumHand.MAIN_HAND);
                    return result == EnumActionResult.SUCCESS ? 1 : 0;
                }
            }
        }

        return 0;
    }

    private ItemStack getFirstStackHasBlock() {
        for (int i = 0; i < inItems.getSlots(); ++i) {
            if (!inItems.getStackInSlot(i).isEmpty()) return inItems.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }
}
