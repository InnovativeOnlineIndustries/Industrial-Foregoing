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
package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.PlantSowerConfig;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.bundle.LockableInventoryBundle;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile.WorkAction;

public class PlantSowerTile extends IndustrialAreaWorkingTile<PlantSowerTile> {

    public static DyeColor[] COLORS = new DyeColor[]{DyeColor.RED, DyeColor.YELLOW, DyeColor.LIME, DyeColor.CYAN, DyeColor.WHITE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.BLACK};

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private ItemStackFilter filter;
    @Save
    private LockableInventoryBundle<PlantSowerTile> input;

    public PlantSowerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.PLANT_SOWER, RangeManager.RangeType.TOP_UP, true, PlantSowerConfig.powerPerOperation, blockPos, blockState);
        addFilter(this.filter = new ItemStackFilter("filter", 9) {
            @Override
            public void onContentChanged() {
                super.onContentChanged();
                markForUpdate();
            }
        });
        int pos = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                FilterSlot slot = new FilterSlot<>(45 + x * 18, 21 + y * 18, pos, ItemStack.EMPTY);
                slot.setColor(COLORS[pos]);
                this.filter.setFilter(pos, slot);
                ++pos;
            }
        }
        addBundle(this.input = new LockableInventoryBundle<>(this, new SidedInventoryComponent<PlantSowerTile>("input", 54 + 18 * 3, 22, 9, 0).
                setColor(DyeColor.CYAN).
                setInputFilter((itemStack, integer) -> itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof IPlantable).
                setRange(3, 3).
                setComponentHarness(this), 118, 84, false));
        this.maxProgress = PlantSowerConfig.maxProgress;
        this.powerPerOperation = PlantSowerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (isLoaded(pos) && (this.level.isEmptyBlock(pos) || this.level.getBlockState(pos).is(Blocks.WATER)) && hasEnergy(powerPerOperation)) {
            int slot = getFilteredSlot(pos);
            ItemStack stack = ItemStack.EMPTY;
            for (int i = 0; i < input.getInventory().getSlots(); i++) {
                if (input.getInventory().getStackInSlot(i).isEmpty()) continue;
                if (filter.getFilterSlots()[slot].getFilter().isEmpty() || filter.getFilterSlots()[slot].getFilter().sameItem(input.getInventory().getStackInSlot(i))) {
                    stack = input.getInventory().getStackInSlot(i);
                    break;
                }
            }
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IPlantable) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (this.level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, (IPlantable) block)) {
                    if (this.level.setBlockAndUpdate(pos, ((IPlantable) block).getPlant(level, pos))) {
                        stack.shrink(1);
                        increasePointer();
                        return new WorkAction(0.2f, powerPerOperation);
                    }
                }
            }
        }
        increasePointer();
        if (hasEnergy(powerPerOperation)) return new WorkAction(0.4f, powerPerOperation / 50);
        return new WorkAction(1f, 0);
    }

    private int getFilteredSlot(BlockPos pos) {
        int radius = hasAugmentInstalled(RangeAddonItem.RANGE) ? (int) AugmentWrapper.getType(getInstalledAugments(RangeAddonItem.RANGE).get(0), RangeAddonItem.RANGE) + 1 : 0;
        if (radius == 0) {
            for (int i = 0; i < input.getInventory().getSlots(); ++i) {
                if (!input.getInventory().getStackInSlot(i).isEmpty()) {
                    return i;
                }
            }
        }
        int x = Math.round(1.49F * (pos.getX() - this.worldPosition.getX()) / radius);
        int z = Math.round(1.49F * (pos.getZ() - this.worldPosition.getZ()) / radius);
        return 4 + x + 3 * z;
    }

    @Override
    protected EnergyStorageComponent<PlantSowerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(PlantSowerConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public PlantSowerTile getSelf() {
        return this;
    }
}
