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
package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.core.DissolutionChamberConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.bundle.LockableInventoryBundle;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class DissolutionChamberTile extends IndustrialProcessingTile<DissolutionChamberTile> {

    private int maxProgress;
    private int powerPerTick;

    @Save
    private LockableInventoryBundle<DissolutionChamberTile> input;
    @Save
    private SidedFluidTankComponent<DissolutionChamberTile> inputFluid;
    @Save
    private SidedInventoryComponent<DissolutionChamberTile> output;
    @Save
    private SidedFluidTankComponent<DissolutionChamberTile> outputFluid;
    private DissolutionChamberRecipe currentRecipe;

    public DissolutionChamberTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleCore.DISSOLUTION_CHAMBER, 102, 41, blockPos, blockState);
        int slotSpacing = 22;
        this.addBundle(this.input = new LockableInventoryBundle<>(this, new SidedInventoryComponent<DissolutionChamberTile>("input", 34, 19, 8, 0).
                setColor(DyeColor.LIGHT_BLUE).
                setSlotPosition(integer -> getSlotPos(integer)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this).
                setInputFilter(((stack, integer) -> !canIncrease())).
                setOnSlotChanged((stack, integer) -> checkForRecipe()), 100, 64, false));
        this.addTank(this.inputFluid = (SidedFluidTankComponent<DissolutionChamberTile>) new SidedFluidTankComponent<DissolutionChamberTile>("input_fluid", DissolutionChamberConfig.maxInputTankSize, 33 + slotSpacing, 18 + slotSpacing, 1).
                setColor(DyeColor.LIME).
                setTankType(FluidTankComponent.Type.SMALL).
                setComponentHarness(this).
                setOnContentChange(() -> checkForRecipe())
        );
        this.addInventory(this.output = (SidedInventoryComponent<DissolutionChamberTile>) new SidedInventoryComponent<DissolutionChamberTile>("output", 129, 22, 3, 2).
                setColor(DyeColor.ORANGE).
                setRange(1, 3).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this));
        this.addTank(this.outputFluid = (SidedFluidTankComponent<DissolutionChamberTile>) new SidedFluidTankComponent<DissolutionChamberTile>("output_fluid", DissolutionChamberConfig.maxOutputTankSize, 149, 20, 3).
                setColor(DyeColor.MAGENTA).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.DRAIN));
        this.maxProgress = DissolutionChamberConfig.maxProgress;
        this.powerPerTick = DissolutionChamberConfig.powerPerTick;
    }

    private void checkForRecipe() {
        if (isServer()) {
            if (currentRecipe != null && currentRecipe.matches(input.getInventory(), inputFluid)) {
                return;
            }
            currentRecipe = RecipeUtil.getRecipes(this.level, DissolutionChamberRecipe.SERIALIZER.getRecipeType()).stream().filter(dissolutionChamberRecipe -> dissolutionChamberRecipe.matches(input.getInventory(), inputFluid)).findFirst().orElse(null);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        checkForRecipe();
    }

    @Override
    public boolean canIncrease() {
        return currentRecipe != null && ItemHandlerHelper.insertItem(output, currentRecipe.output.copy(), true).isEmpty() && (currentRecipe.outputFluid == null || outputFluid.fillForced(currentRecipe.outputFluid.copy(), IFluidHandler.FluidAction.SIMULATE) == currentRecipe.outputFluid.getAmount());
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            if (currentRecipe != null) {
                DissolutionChamberRecipe dissolutionChamberRecipe = currentRecipe;
                inputFluid.drainForced(dissolutionChamberRecipe.inputFluid, IFluidHandler.FluidAction.EXECUTE);
                for (int i = 0; i < input.getInventory().getSlots(); i++) {
                    input.getInventory().getStackInSlot(i).shrink(1);
                }
                if (dissolutionChamberRecipe.outputFluid != null && !dissolutionChamberRecipe.outputFluid.isEmpty())
                    outputFluid.fillForced(dissolutionChamberRecipe.outputFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                ItemStack outputStack = dissolutionChamberRecipe.output.copy();
                outputStack.getItem().onCraftedBy(outputStack, this.level, null);
                ItemHandlerHelper.insertItem(output, outputStack, false);
                checkForRecipe();
            }
        };
    }

    @Override
    protected EnergyStorageComponent<DissolutionChamberTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(DissolutionChamberConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return powerPerTick;
    }

    @Override
    public int getMaxProgress() {
        return currentRecipe != null ? currentRecipe.processingTime : maxProgress;
    }

    public static Pair<Integer, Integer> getSlotPos(int slot) {
        int slotSpacing = 22;
        int offset = 2;
        switch (slot) {
            case 1:
                return Pair.of(slotSpacing, -offset);
            case 2:
                return Pair.of(slotSpacing * 2, 0);
            case 3:
                return Pair.of(-offset, slotSpacing);
            case 4:
                return Pair.of(slotSpacing * 2 + offset, slotSpacing);
            case 5:
                return Pair.of(0, slotSpacing * 2);
            case 6:
                return Pair.of(slotSpacing, slotSpacing * 2 + offset);
            case 7:
                return Pair.of(slotSpacing * 2, slotSpacing * 2);
            default:
                return Pair.of(0, 0);
        }
    }

    @Nonnull
    @Override
    public DissolutionChamberTile getSelf() {
        return this;
    }
}
