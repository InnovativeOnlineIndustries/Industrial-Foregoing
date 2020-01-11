package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.item.RangeAddonItem;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.augment.IAugment;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidCollectorTile extends IndustrialAreaWorkingTile<FluidCollectorTile> {

    @Save
    private SidedFluidTankComponent<FluidCollectorTile> tank;

    public FluidCollectorTile() {
        super(ModuleResourceProduction.FLUID_COLLECTOR, RangeManager.RangeType.BEHIND);
        this.addTank(this.tank = (SidedFluidTankComponent<FluidCollectorTile>) new SidedFluidTankComponent<FluidCollectorTile>("output", 16000, 43, 20, 0)
                .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
                .setComponentHarness(this)
        );
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(1000)) {
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
                    return new WorkAction(1, 1000);
                }
            }
        }
        increasePointer();
        return new WorkAction(1, 0);
    }

    @Override
    public boolean canAcceptAugment(IAugment augment) {
        if (augment.getAugmentType().equals(RangeAddonItem.RANGE)) return false;
        return super.canAcceptAugment(augment);
    }

    @Nonnull
    @Override
    public FluidCollectorTile getSelf() {
        return this;
    }
}
