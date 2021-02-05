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

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.PlantGathererConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class PlantGathererTile extends IndustrialAreaWorkingTile<PlantGathererTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedInventoryComponent<PlantGathererTile> output;
    @Save
    private SidedFluidTankComponent<PlantGathererTile> tank;

    public PlantGathererTile() {
        super(ModuleAgricultureHusbandry.PLANT_GATHERER, RangeManager.RangeType.BEHIND, true, PlantGathererConfig.powerPerOperation);
        addInventory(output = (SidedInventoryComponent<PlantGathererTile>) new SidedInventoryComponent<PlantGathererTile>("output", 70, 22, 3 * 5, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setComponentHarness(this));
        addTank(tank = (SidedFluidTankComponent<PlantGathererTile>) new SidedFluidTankComponent<PlantGathererTile>("sludge", PlantGathererConfig.maxSludgeTankSize, 45, 20, 1)
                .setColor(DyeColor.MAGENTA)
                .setComponentHarness(this));
        this.maxProgress = PlantGathererConfig.maxProgress;
        this.powerPerOperation = PlantGathererConfig.powerPerOperation;
    }

    @Override
    public IndustrialWorkingTile.WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            int amount = Math.max(1, BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox()).size() / 4);
            for (int i = 0; i < amount; i++) {
                if (isLoaded(getPointedBlockPos()) && !ItemStackUtils.isInventoryFull(output)) {
                    Optional<PlantRecollectable> optional = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.getValues().stream().filter(plantRecollectable -> plantRecollectable.canBeHarvested(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))).findFirst();
                    if (optional.isPresent()) {
                        List<ItemStack> drops = optional.get().doHarvestOperation(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()));
                        tank.fill(new FluidStack(ModuleCore.SLUDGE.getSourceFluid(), 10 * drops.size()), IFluidHandler.FluidAction.EXECUTE);
                        drops.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                        if (optional.get().shouldCheckNextPlant(this.world, getPointedBlockPos(), this.world.getBlockState(getPointedBlockPos()))) {
                            increasePointer();
                        }
                        return new WorkAction(0.3f, powerPerOperation);
                    }
                }
                increasePointer();
            }
        }
        increasePointer();
        return new WorkAction(1f, 0);
    }

    @Override
    protected EnergyStorageComponent<PlantGathererTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(PlantGathererConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public PlantGathererTile getSelf() {
        return this;
    }
}
