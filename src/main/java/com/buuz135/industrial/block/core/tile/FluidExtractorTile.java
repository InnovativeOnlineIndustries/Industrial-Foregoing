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

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.core.FluidExtractorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class FluidExtractorTile extends IndustrialAreaWorkingTile<FluidExtractorTile> {

    public static HashMap<DimensionType, HashMap<ChunkPos, HashMap<BlockPos, FluidExtractionProgress>>> EXTRACTION = new HashMap<>();

    private int maxProgress;
    private int powerPerOperation;
    private FluidExtractorRecipe currentRecipe;
    @Save
    private SidedFluidTankComponent<FluidExtractorTile> tank;

    public FluidExtractorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleCore.FLUID_EXTRACTOR, RangeManager.RangeType.BEHIND, false, FluidExtractorConfig.powerPerOperation, blockPos, blockState);
        addTank(tank = (SidedFluidTankComponent<FluidExtractorTile>) new SidedFluidTankComponent<FluidExtractorTile>("latex", FluidExtractorConfig.maxLatexTankSize, 43, 20, 0).
                setColor(DyeColor.LIGHT_GRAY).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this)
        );
        this.maxProgress = FluidExtractorConfig.maxProgress;
        this.powerPerOperation = FluidExtractorConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (isLoaded(pos) && !this.level.isEmptyBlock(pos) && this.tank.getFluidAmount() < this.tank.getCapacity()) {
            if (currentRecipe == null || !currentRecipe.matches(this.level, pos) || currentRecipe.defaultRecipe)
                currentRecipe = findRecipe(this.level, pos);
            if (currentRecipe != null) {//GetDimensionType
                FluidExtractionProgress extractionProgress = EXTRACTION.computeIfAbsent(this.level.dimensionType(), dimensionType -> new HashMap<>()).computeIfAbsent(this.level.getChunkAt(pos).getPos(), chunkPos -> new HashMap<>()).computeIfAbsent(pos, pos1 -> new FluidExtractionProgress(this.level));
                if (currentRecipe.output.getFluid().isSame(ModuleCore.LATEX.getSourceFluid().get())) {
                    tank.fillForced(new FluidStack(currentRecipe.output.getFluid(), currentRecipe.output.getAmount() * (hasEnergy(powerPerOperation) ? 3 : 1)), IFluidHandler.FluidAction.EXECUTE);
                } else {
                    tank.fillForced(currentRecipe.output.copy(), IFluidHandler.FluidAction.EXECUTE);
                }
                if (this.level.random.nextDouble() <= currentRecipe.breakChance) {
                    extractionProgress.setProgress(extractionProgress.getProgress() + 1);
                }
                if (extractionProgress.getProgress() > 7) {
                    extractionProgress.setProgress(0);
                    this.level.setBlockAndUpdate(pos, currentRecipe.result.defaultBlockState());
                    if (currentRecipe.output.getFluid().isSame(ModuleCore.LATEX.getSourceFluid().get())) {
                        tank.fillForced(new FluidStack(currentRecipe.output.getFluid(), currentRecipe.output.getAmount() * (hasEnergy(powerPerOperation) ? 200 : 1)), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
                if (hasEnergy(powerPerOperation)) return new WorkAction(0.4f, powerPerOperation);
                return new WorkAction(1f, 0);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected EnergyStorageComponent<FluidExtractorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FluidExtractorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nullable
    public FluidExtractorRecipe findRecipe(Level world, BlockPos pos) {
        Collection<FluidExtractorRecipe> recipeList = RecipeUtil.getRecipes(world, (RecipeType<FluidExtractorRecipe>) ModuleCore.FLUID_EXTRACTOR_TYPE.get());
        for (FluidExtractorRecipe recipe : recipeList) {
            if (!recipe.defaultRecipe && recipe.matches(world, pos)) return recipe;
        }
        for (FluidExtractorRecipe recipe : recipeList) {
            if (recipe.defaultRecipe && recipe.matches(world, pos)) return recipe;
        }
        return null;
    }

    @Nonnull
    @Override
    public FluidExtractorTile getSelf() {
        return this;
    }

    public static class FluidExtractionProgress {

        private int progress;
        private int breakID;

        public FluidExtractionProgress(Level world) {
            this.progress = 0;
            this.breakID = world.random.nextInt();
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getBreakID() {
            return breakID;
        }

    }
}
