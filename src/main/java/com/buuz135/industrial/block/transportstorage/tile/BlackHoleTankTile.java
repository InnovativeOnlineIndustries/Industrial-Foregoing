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
package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.capability.tile.BigSidedFluidTankComponent;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.NumberUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;

public class BlackHoleTankTile extends BHTile<BlackHoleTankTile> {

    @Save
    public BigSidedFluidTankComponent<BlackHoleTankTile> tank;
    @Save
    private ItemStackFilter filter;
    private boolean isEmpty;

    public BlackHoleTankTile(BasicTileBlock<BlackHoleTankTile> base, BlockEntityType<?> type,  Rarity rarity, BlockPos blockPos, BlockState blockState) {
        super(base, type, blockPos, blockState);
        this.addTank(this.tank = (BigSidedFluidTankComponent<BlackHoleTankTile>) new BigSidedFluidTankComponent<BlackHoleTankTile>("tank", BlockUtils.getFluidAmountByRarity(rarity), 20, 20, 0) {
            @Override
            public void sync() {
                syncObject(tank);
            }
        }
                .setColor(DyeColor.BLUE)
                .setValidator(fluidStack ->  filter.getFilterSlots()[0].getFilter().isEmpty() ? true : filter.getFilterSlots()[0].getFilter().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(iFluidHandlerItem -> iFluidHandlerItem.getFluidInTank(0).isFluidEqual(fluidStack)).orElse(false)
        ));
        this.addFilter(filter = new ItemStackFilter("filter" , 1));
        FilterSlot slot = new FilterSlot<>(79, 60 , 0, ItemStack.EMPTY);
        slot.setColor(DyeColor.CYAN);
        this.filter.setFilter(0, slot);
        this.isEmpty = true;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, BlackHoleTankTile blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
        if (isEmpty != (tank.getFluidAmount() == 0)){
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), Block.UPDATE_ALL);
        }
        isEmpty = tank.getFluidAmount() == 0;
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getDisplayStack() {
        if (tank.getFluidAmount() > 0) {
            ItemStack filledBucket = FluidUtil.getFilledBucket(tank.getFluid());
            if(!filledBucket.isEmpty()) return filledBucket;
        }
        return new ItemStack(Items.BUCKET);
    }

    @Override
    public String getFormatedDisplayAmount() {
        return NumberUtils.getFormatedBigNumber(tank.getFluidAmount() / 1000) + " b";
    }

    @Nonnull
    @Override
    public BlackHoleTankTile getSelf() {
        return this;
    }

    public SidedFluidTankComponent<BlackHoleTankTile> getTank() {
        return tank;
    }
}
