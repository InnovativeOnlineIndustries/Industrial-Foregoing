package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.client.infopiece.BlackHoleInfoPiece;
import com.buuz135.industrial.proxy.client.infopiece.IHasDisplayStack;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class BlackHoleTankTile extends CustomSidedTileEntity implements IHasDisplayStack {

    private IFluidTank tank;

    public BlackHoleTankTile() {
        super(BlackHoleTankTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = new FluidTank(Integer.MAX_VALUE) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                forceSync();
                BlackHoleTankTile.this.markDirty();
            }
        };
        this.addFluidTank(tank, EnumDyeColor.CYAN, "Tank", new BoundingRectangle(6, 25, 18, 54));

    }

    @Override
    protected void innerUpdate() {

    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    public ItemStack getItemStack() {
        return tank.getFluid() == null ? new ItemStack(Items.BUCKET) : FluidUtil.getFilledBucket(tank.getFluid());
    }

    @Override
    public int getAmount() {
        return tank.getFluidAmount();
    }

    @Override
    public String getDisplayNameUnlocalized() {
        return tank.getFluid() == null ? "text.display.empty" : tank.getFluid().getLocalizedName();
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = super.getGuiContainerPieces(container);
        list.add(new BlackHoleInfoPiece(this, 18 * 2 + 8, 25));
        return list;
    }

    public IFluidTank getTank() {
        return tank;
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
