package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class TreeFluidExtractorTile extends SidedTileEntity {

    private IFluidTank tank;

    public TreeFluidExtractorTile() {
        super(TreeFluidExtractorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.LATEX, 8000, EnumDyeColor.GRAY, "Latex tank", new BoundingRectangle(17, 25, 18, 54));
    }

    @Override
    protected void createAddonsInventory() {

    }

    @Override
    protected void innerUpdate() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return;
        if (this.getWorld().isRemote) return;
        if (this.world.getWorldTime() % 5 == 0 && BlockUtils.isLog(this.world, this.pos.offset(this.getFacing().getOpposite())))
            tank.fill(new FluidStack(FluidsRegistry.LATEX, 1), true);
    }


}
