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

import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.api.IAcceptsAdultFilter;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.List;

public class MobSlaughterFactoryTile extends WorkingAreaElectricMachine implements IAcceptsAdultFilter {

    private IFluidTank outMeat;
    private IFluidTank outPink;

    public MobSlaughterFactoryTile() {
        super(MobSlaughterFactoryTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outMeat = this.addFluidTank(FluidsRegistry.MEAT, 8000, EnumDyeColor.BROWN, "Meat tank", new BoundingRectangle(46, 25, 18, 54));
        outPink = this.addSimpleFluidTank(8000, "Pink Slime Tank", EnumDyeColor.PINK, 46 + 20, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        mobs.removeIf(entityLiving -> entityLiving.isDead);
        if (hasAddon()) mobs.removeIf(entityLiving -> entityLiving instanceof EntityAgeable && entityLiving.isChild());
        if (mobs.size() == 0) return 0;
        EntityLiving mob = mobs.get(this.getWorld().rand.nextInt(mobs.size()));
        double health = mob.getHealth();
        mob.attackEntityFrom(CommonProxy.custom, BlockRegistry.mobSlaughterFactoryBlock.getDamage());
        health -= mob.getHealth();
        if (health > 0) {
            mob.setDropItemsWhenDead(false);
            this.outMeat.fill(new FluidStack(FluidsRegistry.MEAT, (int) (health * BlockRegistry.mobSlaughterFactoryBlock.getMeatValue())), true);
            this.outPink.fill(new FluidStack(FluidsRegistry.PINK_SLIME, (int) health * (mob instanceof EntityAnimal ? 8 : 1)), true);
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.fillItemFromTank(fluidItems, outMeat);
    }

    @Override
    public boolean hasAddon() {
        return this.hasAddon(AdultFilterAddonItem.class);
    }
}
