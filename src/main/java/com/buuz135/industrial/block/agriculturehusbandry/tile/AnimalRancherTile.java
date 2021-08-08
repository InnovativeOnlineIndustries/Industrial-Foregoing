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

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.AnimalRancherConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class AnimalRancherTile extends IndustrialAreaWorkingTile<AnimalRancherTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedFluidTankComponent<AnimalRancherTile> tank;
    @Save
    private SidedInventoryComponent<AnimalRancherTile> output;

    public AnimalRancherTile() {
        super(ModuleAgricultureHusbandry.ANIMAL_RANCHER, RangeManager.RangeType.BEHIND, true, AnimalRancherConfig.powerPerOperation);
        this.addTank(tank = (SidedFluidTankComponent<AnimalRancherTile>) new SidedFluidTankComponent<AnimalRancherTile>("fluid_output", AnimalRancherConfig.maxTankSize, 47, 20, 0).
                setColor(DyeColor.WHITE).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<AnimalRancherTile>) new SidedInventoryComponent<AnimalRancherTile>("output", 74, 22, 5 * 3, 1).
                setColor(DyeColor.ORANGE).
                setRange(5, 3).
                setComponentHarness(this)
        );
        this.maxProgress = AnimalRancherConfig.maxProgress;
        this.powerPerOperation = AnimalRancherConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            List<CreatureEntity> mobs = this.world.getEntitiesWithinAABB(CreatureEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                for (CreatureEntity mob : mobs) {
                    FakePlayer player = IndustrialForegoing.getFakePlayer(world, mob.getPosition()); //getPosition
                    //SHEAR INTERACTION
                    ItemStack shears = new ItemStack(Items.SHEARS);
                    if (mob instanceof IForgeShearable && ((IForgeShearable) mob).isShearable(shears, this.world, mob.getPosition())) { //getPosition
                        List<ItemStack> items = ((IForgeShearable) mob).onSheared(player, shears, this.world, mob.getPosition(), 0); //getPosition
                        items.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                        if (items.size() > 0) {
                            return new WorkAction(0.35f, powerPerOperation);
                        }
                    }
                    if (mob instanceof SquidEntity && !ItemStackUtils.isInventoryFull(output) && world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean()) {
                        ItemHandlerHelper.insertItem(output, new ItemStack(Items.BLACK_DYE), false);
                        return new WorkAction(0.35f, powerPerOperation);
                    }
                    //BUCKET INTERACTION
                    if (mob instanceof AnimalEntity && tank.getFluidAmount() + 1000 <= tank.getCapacity()) {
                        player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
                        if (((AnimalEntity) mob).func_230254_b_(player, Hand.MAIN_HAND).isSuccessOrConsume()) { //ProcessInteract
                            ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
                            IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                            if (fluidHandlerItem != null) {
                                tank.fillForced(fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                                player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                return new WorkAction(0.35f, powerPerOperation);
                            }
                            player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<AnimalRancherTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(AnimalRancherConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public AnimalRancherTile getSelf() {
        return this;
    }

}
