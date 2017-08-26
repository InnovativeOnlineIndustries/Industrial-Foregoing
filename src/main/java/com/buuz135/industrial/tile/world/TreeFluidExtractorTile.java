package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

public class TreeFluidExtractorTile extends SidedTileEntity {

    private IFluidTank tank;
    private int tick;
    private int progress;
    private int id;

    public TreeFluidExtractorTile() {
        super(TreeFluidExtractorTile.class.getName().hashCode());
        tick = 0;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.LATEX, 8000, EnumDyeColor.GRAY, "Latex tank", new BoundingRectangle(17, 25, 18, 54));
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    protected void innerUpdate() {
        if (WorkUtils.isDisabled(this.getBlockType())) return;
        if (this.getWorld().isRemote) return;
        if (!BlockUtils.isLog(this.world, this.pos.offset(this.getFacing().getOpposite()))) progress = 0;
        if (tick % 5 == 0 && BlockUtils.isLog(this.world, this.pos.offset(this.getFacing().getOpposite()))) {
            tank.fill(new FluidStack(FluidsRegistry.LATEX, 1), true);
            if (id == 0) id = this.world.rand.nextInt();
            if (world.rand.nextDouble() <= 0.005) ++progress;
            if (progress > 7) {
                progress = 0;
                this.world.setBlockToAir(this.pos.offset(this.getFacing().getOpposite()));
            }
            if (tick > 404 && progress > 0) {
                this.world.sendBlockBreakProgress(this.world.rand.nextInt(), this.pos.offset(this.getFacing().getOpposite()), progress - 1);
                tick = 0;
            }
        }
        ++tick;
    }


    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, tank);
    }

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }
}
