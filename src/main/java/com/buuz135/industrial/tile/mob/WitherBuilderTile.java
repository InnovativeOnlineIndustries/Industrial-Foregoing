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
package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import org.jetbrains.annotations.NotNull;

public class WitherBuilderTile extends WorkingAreaElectricMachine {

    private ItemStackHandler top;
    private ItemStackHandler middle;
    private ItemStackHandler bottom;

    public WitherBuilderTile() {
        super(WitherBuilderTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        int left = 75;
        top = new ItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(top, EnumDyeColor.BLACK, "wither_skulls", new BoundingRectangle(left, 25, 18 * 3, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Items.SKULL, 1, 1));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(top, "wither_skulls");
        middle = new ItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(middle, EnumDyeColor.BROWN, "soulsand", new BoundingRectangle(left, 25 + 18, 18 * 3, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Blocks.SOUL_SAND));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(middle, "soulsand_middle");
        bottom = new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                WitherBuilderTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new ColoredItemHandler(bottom, EnumDyeColor.BROWN, "soulsand", new BoundingRectangle(left + 18, 25 + 18 * 2, 18, 18)) {
            @Override
            public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
                return stack.isItemEqual(new ItemStack(Blocks.SOUL_SAND));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(middle, "soulsand_bottom");
    }

    @Override
    public float work() {
        BlockPos pos = this.pos.add(0, 2, 0);
        float power = 0;
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
            this.world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
            getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
            power += 1 / 7f;
        }
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
            for (int i = 0; i < 3; ++i) {
                BlockPos temp = pos.add(i - 1, 1, 0);
                if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
                    this.world.setBlockState(temp, Blocks.SOUL_SAND.getDefaultState());
                    getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
                    power += 1 / 7f;
                }
            }
        }
        if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
            boolean secondRow = true;
            for (int i = 0; i < 3; ++i) {
                BlockPos temp = pos.add(i - 1, 1, 0);
                if (!this.world.getBlockState(temp).getBlock().equals(Blocks.SOUL_SAND)) {
                    secondRow = false;
                    break;
                }
            }
            if (secondRow) {
                for (int i = 0; i < 3; ++i) {
                    BlockPos temp = pos.add(i - 1, 2, 0);
                    if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, top, new ItemStack(Items.SKULL, 1, 1)).isEmpty() && this.world.getBlockState(temp.add(0, -1, 0)).getBlock().equals(Blocks.SOUL_SAND)) {
                        FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
                        ItemStack stack = getDefaultOrFind(i, top, new ItemStack(Items.SKULL, 1, 1));
                        player.setHeldItem(EnumHand.MAIN_HAND, stack);
                        EnumActionResult result = ForgeHooks.onPlaceItemIntoWorld(stack, player, world, temp, EnumFacing.UP, 0, 0, 0, EnumHand.MAIN_HAND);
                        if (result == EnumActionResult.SUCCESS) power += 1 / 7f;
                    }
                }
            }
        }
        return power;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(1, 1, 0).offset(new BlockPos(0, 3, 0));
    }

    public ItemStack getDefaultOrFind(int i, ItemStackHandler handler, ItemStack filter) {
        if (handler.getStackInSlot(i).isItemEqual(filter)) return handler.getStackInSlot(i);
        for (ItemStackHandler h : new ItemStackHandler[]{top, middle, bottom}) {
            for (int s = 0; s < h.getSlots(); ++s) {
                if (h.getStackInSlot(s).isItemEqual(filter)) return h.getStackInSlot(s);
            }
        }
        return ItemStack.EMPTY;
    }
}
