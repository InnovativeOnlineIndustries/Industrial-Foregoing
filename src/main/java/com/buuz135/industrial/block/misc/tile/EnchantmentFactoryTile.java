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
package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentFactoryConfig;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EnchantmentFactoryTile extends IndustrialProcessingTile<EnchantmentFactoryTile> {

    private static final int XP_30 = 1410;

    @Save
    private SidedFluidTankComponent<EnchantmentFactoryTile> tank;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> inputFirst;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> output;

    public EnchantmentFactoryTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleMisc.ENCHANTMENT_FACTORY, 100, 40, blockPos, blockState);
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentFactoryTile>) new SidedFluidTankComponent<EnchantmentFactoryTile>("essence", EnchantmentFactoryConfig.tankSize, 34, 20, 0).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().is(IndustrialTags.Fluids.EXPERIENCE))
        );
        this.addInventory(inputFirst = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("input", 70, 40, 1, 1).
                setColor(DyeColor.BLUE).
                setInputFilter((stack, integer) -> (!isEnchanted(stack) && stack.isEnchantable()) || stack.getItem().equals(Items.BOOK)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("output", 135, 40, 1, 2).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @Override
    public boolean canIncrease() {
        return this.tank.getFluidAmount() >= XP_30 * 20 && !this.inputFirst.getStackInSlot(0).isEmpty() && this.output.getStackInSlot(0).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack output = EnchantmentHelper.enchantItem(this.level.random, this.inputFirst.getStackInSlot(0).copy(), 50, true);
            this.inputFirst.setStackInSlot(0, ItemStack.EMPTY);
            this.output.setStackInSlot(0, output);
            this.tank.drainForced(XP_30 * 20, IFluidHandler.FluidAction.EXECUTE);
        };
    }

    @Override
    protected int getTickPower() {
        return EnchantmentFactoryConfig.powerPerTick;
    }

    @Override
    public EnchantmentFactoryTile getSelf() {
        return this;
    }

    private boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem;
    }

    @Override
    protected EnergyStorageComponent<EnchantmentFactoryTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(EnchantmentFactoryConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return EnchantmentFactoryConfig.maxProgress;
    }
}
