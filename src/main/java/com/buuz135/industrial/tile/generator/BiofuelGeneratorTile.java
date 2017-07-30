package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomGeneratorMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class BiofuelGeneratorTile extends CustomGeneratorMachine {

    private IFluidTank tank;

    public BiofuelGeneratorTile() {
        super(BiofuelGeneratorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.BIOFUEL, 8000, EnumDyeColor.PURPLE, "Biofuel tank", new BoundingRectangle(58, 25, 18, 54));
    }

    @Override
    protected long consumeFuel() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (tank.getFluid() != null && tank.getFluidAmount() > 1) {
            tank.drain(1, true);
            return 160 * 7;
        }
        return 0;
    }

    @Override
    public long getEnergyFillRate() {
        return 160;
    }
}
