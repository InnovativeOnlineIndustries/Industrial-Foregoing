package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.FluidCollectorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidCollectorTile extends IndustrialAreaWorkingTile<FluidCollectorTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<FluidCollectorTile> tank;

    public FluidCollectorTile() {
        super(ModuleResourceProduction.FLUID_COLLECTOR, RangeManager.RangeType.BEHIND, false);
        this.addTank(this.tank = (SidedFluidTankComponent<FluidCollectorTile>) new SidedFluidTankComponent<FluidCollectorTile>("output", FluidCollectorConfig.maxOutputTankSize, 43, 20, 0)
                .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
                .setComponentHarness(this)
        );
        this.getMaxProgress = FluidCollectorConfig.maxProgress;
        this.getPowerPerOperation = FluidCollectorConfig.maxOutputTankSize;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            if (isLoaded(getPointedBlockPos()) && !world.isAirBlock(getPointedBlockPos()) && BlockUtils.canBlockBeBroken(this.world, getPointedBlockPos()) && world.getFluidState(getPointedBlockPos()).isSource()) {
                Fluid fluid = world.getFluidState(getPointedBlockPos()).getFluid();
                if (tank.isEmpty() || (tank.getFluid().getFluid().isEquivalentTo(fluid) && tank.getFluidAmount() + FluidAttributes.BUCKET_VOLUME <= tank.getCapacity())) {
                    if (world.getBlockState(getPointedBlockPos()).has(BlockStateProperties.WATERLOGGED)) {
                        world.setBlockState(getPointedBlockPos(), world.getBlockState(getPointedBlockPos()).with(BlockStateProperties.WATERLOGGED, false));
                    } else {
                        world.setBlockState(getPointedBlockPos(), Blocks.AIR.getDefaultState());
                    }
                    tank.fillForced(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
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
        return () -> new NBTEnergyHandler(this, FluidCollectorConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }

    @Nonnull
    @Override
    public FluidCollectorTile getSelf() {
        return this;
    }
}
