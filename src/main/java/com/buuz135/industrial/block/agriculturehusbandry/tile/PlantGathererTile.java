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
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile.WorkAction;

public class PlantGathererTile extends IndustrialAreaWorkingTile<PlantGathererTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedInventoryComponent<PlantGathererTile> output;
    @Save
    private SidedFluidTankComponent<PlantGathererTile> tank;
    @Save
    private SidedFluidTankComponent<PlantGathererTile> ether;
    @Save
    private ProgressBarComponent<PlantGathererTile> etherBar;

    public PlantGathererTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleAgricultureHusbandry.PLANT_GATHERER, RangeManager.RangeType.BEHIND, true, PlantGathererConfig.powerPerOperation, blockPos, blockState);
        addInventory(output = (SidedInventoryComponent<PlantGathererTile>) new SidedInventoryComponent<PlantGathererTile>("output", 78, 22, 3 * 5, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setComponentHarness(this));
        addTank(tank = (SidedFluidTankComponent<PlantGathererTile>) new SidedFluidTankComponent<PlantGathererTile>("sludge", PlantGathererConfig.maxSludgeTankSize, 43, 20, 1)
                .setColor(DyeColor.MAGENTA)
                .setTankAction(FluidTankComponent.Action.DRAIN)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setComponentHarness(this));
        addTank(ether = (SidedFluidTankComponent<PlantGathererTile>) new SidedFluidTankComponent<PlantGathererTile>("ether", PlantGathererConfig.maxEtherTankSize, 43, 57, 2)
                .setColor(DyeColor.CYAN)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setComponentHarness(this)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.ETHER.getSourceFluid().get()))
        );
        addProgressBar(etherBar =  new ProgressBarComponent<PlantGathererTile>(63, 20, 0, 100)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN)
                .setCanReset(hydroponicBedTile -> false)
        );
        this.maxProgress = PlantGathererConfig.maxProgress;
        this.powerPerOperation = PlantGathererConfig.powerPerOperation;
    }

    @Override
    public IndustrialWorkingTile.WorkAction work() {
        if (this.etherBar.getProgress() == 0 && this.ether.getFluidAmount() > 0){
            this.etherBar.setProgress(this.etherBar.getMaxProgress());
            this.ether.drainForced(1, IFluidHandler.FluidAction.EXECUTE);
        }
        if (hasEnergy(powerPerOperation)) {
            int amount = Math.max(1, BlockUtils.getBlockPosInAABB(getWorkingArea().bounds()).size() / 30);
            for (int i = 0; i < amount; i++) {
                BlockPos pointed = getPointedBlockPos();
                if (isLoaded(pointed) && !ItemStackUtils.isInventoryFull(output)) {
                    if (this.etherBar.getProgress() > 0){
                        if (HydroponicBedTile.tryToHarvestAndReplant(this.level, pointed, this.level.getBlockState(pointed), this.output, this.etherBar, this)){
                            tank.fillForced(new FluidStack(ModuleCore.SLUDGE.getSourceFluid().get(), 10), IFluidHandler.FluidAction.EXECUTE);
                            return new WorkAction(0.3f, powerPerOperation);
                        }
                    }else {
                        Optional<PlantRecollectable> optional = IFRegistries.PLANT_RECOLLECTABLES_REGISTRY.get().getValues().stream().filter(plantRecollectable -> plantRecollectable.canBeHarvested(this.level, pointed, this.level.getBlockState(pointed))).findFirst();
                        if (optional.isPresent()) {
                            List<ItemStack> drops = optional.get().doHarvestOperation(this.level, pointed, this.level.getBlockState(pointed));
                            tank.fillForced(new FluidStack(ModuleCore.SLUDGE.getSourceFluid().get(), 10 * drops.size()), IFluidHandler.FluidAction.EXECUTE);
                            drops.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                            if (optional.get().shouldCheckNextPlant(this.level, pointed, this.level.getBlockState(pointed))) {
                                increasePointer();
                            }
                            return new WorkAction(0.3f, powerPerOperation);
                        }
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
