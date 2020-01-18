package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class FluidExtractorTile extends IndustrialAreaWorkingTile<FluidExtractorTile> {

    public static HashMap<DimensionType, HashMap<ChunkPos, HashMap<BlockPos, FluidExtractionProgress>>> EXTRACTION = new HashMap<>();

    private FluidExtractorRecipe currentRecipe;
    @Save
    private SidedFluidTankComponent<FluidExtractorTile> tank;

    public FluidExtractorTile() {
        super(ModuleCore.FLUID_EXTRACTOR, RangeManager.RangeType.BEHIND);
        addTank(tank = (SidedFluidTankComponent<FluidExtractorTile>) new SidedFluidTankComponent<FluidExtractorTile>("latex", 1000, 43, 20, 0).
                setColor(DyeColor.LIGHT_GRAY).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.LATEX.getSourceFluid()))
        );
    }

    @Override
    public WorkAction work() {
        BlockPos pos = getPointedBlockPos();
        if (isLoaded(pos)) {
            if (currentRecipe == null || !currentRecipe.matches(this.world, pos))
                currentRecipe = findRecipe(this.world, pos);
            if (currentRecipe != null) {
                FluidExtractionProgress extractionProgress = EXTRACTION.computeIfAbsent(this.world.dimension.getType(), dimensionType -> new HashMap<>()).computeIfAbsent(this.world.getChunkAt(pos).getPos(), chunkPos -> new HashMap<>()).computeIfAbsent(pos, pos1 -> new FluidExtractionProgress(this.world));
                tank.fillForced(currentRecipe.output.copy(), IFluidHandler.FluidAction.EXECUTE);
                if (this.world.rand.nextDouble() <= currentRecipe.breakChance) {
                    extractionProgress.setProgress(extractionProgress.getProgress() + 1);
                }
                if (extractionProgress.getProgress() > 7) {
                    extractionProgress.setProgress(0);
                    this.world.setBlockState(pos, currentRecipe.result.getDefaultState());
                }
                if (hasEnergy(500)) return new WorkAction(0.4f, 500);
                return new WorkAction(1f, 0);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return 30;
    }

    @Nullable
    public FluidExtractorRecipe findRecipe(World world, BlockPos pos) {
        return RecipeUtil.getRecipes(world, FluidExtractorRecipe.SERIALIZER.getRecipeType()).stream().filter(fluidExtractorRecipe -> fluidExtractorRecipe.matches(world, pos) && !fluidExtractorRecipe.defaultRecipe).findFirst().orElseGet(() -> RecipeUtil.getRecipes(world, FluidExtractorRecipe.SERIALIZER.getRecipeType()).stream().filter(fluidExtractorRecipe -> fluidExtractorRecipe.matches(world, pos)).findFirst().orElse(null));
    }

    @Nonnull
    @Override
    public FluidExtractorTile getSelf() {
        return this;
    }

    public static class FluidExtractionProgress {

        private int progress;
        private int breakID;

        public FluidExtractionProgress(World world) {
            this.progress = 0;
            this.breakID = world.rand.nextInt();
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
