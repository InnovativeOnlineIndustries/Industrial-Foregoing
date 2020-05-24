package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.FluidPlacerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidPlacerTile extends IndustrialAreaWorkingTile<FluidPlacerTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<FluidPlacerTile> tank;

    public FluidPlacerTile() {
        super(ModuleResourceProduction.FLUID_PLACER, RangeManager.RangeType.BEHIND, false);
        this.addTank(this.tank = (SidedFluidTankComponent<FluidPlacerTile>) new SidedFluidTankComponent<FluidPlacerTile>("input", FluidPlacerConfig.maxInputTankSize, 43, 20, 0)
                .setColor(DyeColor.BLUE)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setComponentHarness(this)
        );
        this.getMaxProgress = FluidPlacerConfig.maxProgress;
        this.getPowerPerOperation = FluidPlacerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            if (isLoaded(getPointedBlockPos()) && BlockUtils.canBlockBeBroken(this.world, getPointedBlockPos()) && !world.getFluidState(getPointedBlockPos()).isSource() && tank.getFluidAmount() >= FluidAttributes.BUCKET_VOLUME) {
                if (tank.getFluid().getFluid().isEquivalentTo(Fluids.WATER) && world.getBlockState(getPointedBlockPos()).has(BlockStateProperties.WATERLOGGED) && !world.getBlockState(getPointedBlockPos()).get(BlockStateProperties.WATERLOGGED)) {
                    world.setBlockState(getPointedBlockPos(), world.getBlockState(getPointedBlockPos()).with(BlockStateProperties.WATERLOGGED, true));
                    tank.drainForced(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                } else if (world.isAirBlock(getPointedBlockPos())) {
                    world.setBlockState(getPointedBlockPos(), tank.getFluid().getFluid().getDefaultState().getBlockState());
                    tank.drainForced(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    increasePointer();
                    return new WorkAction(1, getPowerPerOperation);
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, FluidPlacerConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public FluidPlacerTile getSelf() {
        return this;
    }
}
