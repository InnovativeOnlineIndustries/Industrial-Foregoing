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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;

public class BlackHoleTankTile extends BHTile<BlackHoleTankTile> {

    @Save
    public BigSidedFluidTankComponent<BlackHoleTankTile> tank;
    @Save
    private ItemStackFilter filter;
    private boolean isEmpty;

    public BlackHoleTankTile(BasicTileBlock<BlackHoleTankTile> base, Rarity rarity) {
        super(base);
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
    public void tick() {
        super.tick();
        if (isEmpty != (tank.getFluidAmount() == 0)){
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
        isEmpty = tank.getFluidAmount() == 0;
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
        }
        openGui(playerIn);
        return ActionResultType.SUCCESS;
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
