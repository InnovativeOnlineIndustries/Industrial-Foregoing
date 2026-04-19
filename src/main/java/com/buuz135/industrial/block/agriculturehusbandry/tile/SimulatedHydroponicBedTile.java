/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2026, Buuz135
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

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.config.machine.resourceproduction.HydroponicBedConfig;
import com.buuz135.industrial.config.machine.resourceproduction.SimulatedHydroponicBedConfig;
import com.buuz135.industrial.item.HydroponicSimulationProcessorItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SimulatedHydroponicBedTile extends IndustrialWorkingTile<SimulatedHydroponicBedTile> {

    @Save
    private final SidedInventoryComponent<SimulatedHydroponicBedTile> output;

    @Save
    private final SidedInventoryComponent<SimulatedHydroponicBedTile> simulation_slot;
    @Save
    private final SidedInventoryComponent<SimulatedHydroponicBedTile> seed;
    private HydroponicSimulationProcessorItem.Simulation simulation;

    public SimulatedHydroponicBedTile(BlockPos blockPos, BlockState blockState)
    {
        super(ModuleAgricultureHusbandry.SIMULATED_HYDROPONIC_BED, SimulatedHydroponicBedConfig.powerPerOperation, blockPos, blockState);
        addInventory(this.output = (SidedInventoryComponent<SimulatedHydroponicBedTile>) new SidedInventoryComponent<SimulatedHydroponicBedTile>("output", 79 - 18 * 2 + 2, 22, 7 * 3, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(7, 3)
                .setInputFilter((stack, integer) -> false)
        );
        addInventory(this.simulation_slot = (SidedInventoryComponent<SimulatedHydroponicBedTile>) new SidedInventoryComponent<SimulatedHydroponicBedTile>("simulation", 70 + 18 * 2, 80, 1, 1)
                .setColor(DyeColor.LIME)
                .setInputFilter((stack, integer) -> stack.getItem().equals(ModuleAgricultureHusbandry.HYDROPONIC_SIMULATION_PROCESSOR.get()))
                .setOutputFilter((stack, integer) -> false)
                .setOnSlotChanged((itemStack, integer) -> this.simulation = null)
        );
        addInventory(this.seed = (SidedInventoryComponent<SimulatedHydroponicBedTile>) new SidedInventoryComponent<SimulatedHydroponicBedTile>("seed", 70 + 9, 80, 1, 2)
                .setColor(DyeColor.CYAN)
                .setInputFilter((stack, integer) -> true)
                .setOutputFilter((stack, integer) -> false)
        );
    }

    @Override
    public WorkAction work()
    {
        if (hasEnergy(SimulatedHydroponicBedConfig.powerPerOperation))
        {
            ItemStack simulationProcessor = this.simulation_slot.getStackInSlot(0);
            ItemStack seed = this.seed.getStackInSlot(0);
            if (!seed.isEmpty() && !simulationProcessor.isEmpty() && simulationProcessor.getItem() instanceof HydroponicSimulationProcessorItem)
            {
                if (this.simulation == null)
                {
                    this.simulation = new HydroponicSimulationProcessorItem.Simulation(simulationProcessor.getTag().getCompound(HydroponicSimulationProcessorItem.ITEM_NAME));
                }
                if (!ItemStack.isSameItem(seed, this.simulation.getCrop()))
                {
                    return new WorkAction(1, 0);
                }


                // Extract crop and executions from the CompoundTag
                final ItemStack crop = simulation.getCrop();
                final long executions = simulation.getExecutions();

                if (!crop.isEmpty() && executions > 0)
                {
                    double efficiency = Math.floor(HydroponicSimulationProcessorItem.calculateEfficiency(executions) * 100) / 100;

                    List<ItemStack> generatedDrops = new ArrayList<>();

                    for (var simulationStack : simulation.getStats())
                    {
                        ItemStack statStack = simulationStack.stack();
                        long statAmount = simulationStack.amount();

                        double amount = (statAmount / (double) executions) * efficiency;

                        if (amount >= 1)
                        {
                            int fullAmount = (int)Math.floor(amount);
                            ItemStack drop = statStack.copy();
                            drop.setCount(fullAmount);
                            generatedDrops.add(drop);

                            // Handle fractional amounts with probability
                            double fraction = amount - fullAmount;
                            if (fraction > 0 && this.level.random.nextDouble() < fraction)
                            {
                                ItemStack extraDrop = statStack.copy();
                                extraDrop.setCount(1);
                                generatedDrops.add(extraDrop);
                            }
                        } else if (amount > 0 && this.level.random.nextDouble() < amount)
                        {
                            ItemStack drop = statStack.copy();
                            drop.setCount(1);
                            generatedDrops.add(drop);
                        }
                    }

                    // Add drops to output
                    generatedDrops.forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
                    return new WorkAction(1, HydroponicBedConfig.powerPerOperation);
                }
            }
        }

        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return SimulatedHydroponicBedConfig.maxProgress;
    }

    @Nonnull
    @Override
    public SimulatedHydroponicBedTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<SimulatedHydroponicBedTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SimulatedHydroponicBedConfig.maxStoredPower, 10, 20);
    }
}
