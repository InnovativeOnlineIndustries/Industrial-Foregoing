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
import com.buuz135.industrial.capability.tile.BigEnergyHandler;
import com.buuz135.industrial.config.machine.agriculturehusbandry.WitherBuilderConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class WitherBuilderTile extends IndustrialAreaWorkingTile<WitherBuilderTile> {

    @Save
    private SidedInventoryComponent<WitherBuilderTile> top;

    @Save
    private SidedInventoryComponent<WitherBuilderTile> middle;


    public WitherBuilderTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.WITHER_BUILDER, RangeManager.RangeType.TOP_UP, true, WitherBuilderConfig.powerPerOperation, blockPos, blockState);
        this.addInventory(top = (SidedInventoryComponent<WitherBuilderTile>) new SidedInventoryComponent<WitherBuilderTile>("wither_skulls", 64, 25, 3, 0)
                .setColor(DyeColor.BLACK)
                .setInputFilter((itemStack, integer) -> itemStack.getItem().equals(Items.WITHER_SKELETON_SKULL))
                .setSlotLimit(1)
                .setComponentHarness(this)
        );
        this.addInventory(middle = (SidedInventoryComponent<WitherBuilderTile>) new SidedInventoryComponent<WitherBuilderTile>("soulsand", 64, 25 + 18, 4, 1)
                .setColor(DyeColor.BROWN)
                .setInputFilter((itemStack, integer) -> itemStack.getItem().equals(Blocks.SOUL_SAND.asItem()))
                .setSlotPosition(integer -> {
                    if (integer == 3) {
                        return Pair.of(18, 18);
                    }
                    return Pair.of(18 * (integer % 3), 18 * (integer / 3));
                })
                .setSlotLimit(1)
                .setComponentHarness(this)
        );
    }

    @Override
    public WorkAction work() {
        if (!hasEnergy(WitherBuilderConfig.powerPerOperation) || !BlockUtils.canBlockBeBroken(this.level, this.worldPosition, this.getUuid()) || this.level.getDifficulty() == Difficulty.PEACEFUL)
            return new WorkAction(1, 0);
        //CHECK IF THERE ARE WITHERS SPAWNING
        if (this.level.getEntitiesOfClass(WitherBoss.class, getWorkingArea().bounds()).size() > 5) {
            return new WorkAction(1, 0);
        }
        //CHECK IF AREA IS CLEAR
        for (BlockPos blockPos : BlockUtils.getBlockPosInAABB(getWorkingArea().bounds())) {
            if (!this.level.getBlockState(blockPos).isAir()) {
                return new WorkAction(1, 0);
            }
        }
        //CHECK IF WE HAVE ITEMS
        for (ItemStackHandler handler : new ItemStackHandler[]{top, middle}) {
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                if (handler.getStackInSlot(slot).isEmpty()) return new WorkAction(1, 0);
            }
        }
        for (ItemStackHandler handler : new ItemStackHandler[]{top, middle}) {
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                handler.getStackInSlot(slot).shrink(1);
            }
        }
        var wither = (WitherBoss) EntityType.WITHER.create(level);
        BlockPos pos = this.worldPosition.offset(0, 2, 0);
        wither.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5f);
        wither.yBodyRot = this.getFacingDirection().getAxis() != Direction.Axis.X ? 0.0F : 90.0F;
        wither.makeInvulnerable();
        level.addFreshEntity(wither);
        return new WorkAction(1, WitherBuilderConfig.powerPerOperation);
    }

    @Override
    public VoxelShape getWorkingArea() {

        return new RangeManager(this.worldPosition, this.getFacingDirection(), RangeManager.RangeType.TOP_UP) {
            @Override
            public AABB getBox() {
                if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
                    return super.getBox().move(new BlockPos(0, 1, 0)).inflate(0, 1, 1);
                } else {
                    return super.getBox().move(new BlockPos(0, 1, 0)).inflate(1, 1, 0);
                }
            }
        }.get(0);

    }

    @Nonnull
    @Override
    public WitherBuilderTile getSelf() {
        return this;
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<WitherBuilderTile> createEnergyStorage() {
        return new BigEnergyHandler<WitherBuilderTile>(WitherBuilderConfig.maxStoredPower, 10, 20) {
            @Override
            public void sync() {
                WitherBuilderTile.this.syncObject(getEnergyStorage());
            }
        };
    }

    @Override
    public int getMaxProgress() {
        return WitherBuilderConfig.maxProgress;
    }


    public ItemStack getDefaultOrFind(int i, ItemStackHandler handler, ItemStack filter) {
        if (ItemStack.isSameItem(handler.getStackInSlot(i), filter)) return handler.getStackInSlot(i);
        for (ItemStackHandler h : new ItemStackHandler[]{top, middle}) {
            for (int s = 0; s < h.getSlots(); ++s) {
                if (ItemStack.isSameItem(h.getStackInSlot(s), filter)) return h.getStackInSlot(s);
            }
        }
        return ItemStack.EMPTY;
    }
}
