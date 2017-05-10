package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class WaterCondesatorTile extends SidedTileEntity {

    private IFluidTank fluidTank;

    public WaterCondesatorTile() {
        super(WaterCondesatorTile.class.getName().hashCode());
    }

    @Override
    protected void innerUpdate() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return;
        if (this.getWorld().isRemote) return;
        int fillValue = getWaterSources() * 100;
        fluidTank.fill(new FluidStack(FluidRegistry.WATER, fillValue), true);

    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        fluidTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(16, 25, 18, 54));
    }

    private int getWaterSources() {
        int sources = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            IBlockState state = this.world.getBlockState(this.getPos().offset(facing));
            if (state.getBlock().equals(FluidRegistry.WATER.getBlock()) && state.getBlock().getMetaFromState(state) == 0)
                ++sources;
        }
        return sources;
    }
}
