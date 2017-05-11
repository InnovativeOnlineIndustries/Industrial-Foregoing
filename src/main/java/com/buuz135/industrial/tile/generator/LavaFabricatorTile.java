package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.tile.CustomElectricMachine;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public class LavaFabricatorTile extends CustomElectricMachine{

    private IFluidTank tank;

    public LavaFabricatorTile() {
        super(LavaFabricatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidRegistry.LAVA,8000, EnumDyeColor.ORANGE,"Lava tank", new BoundingRectangle(50, 25, 18, 54));
    }

    @Override
    protected float performWork() {
        if (tank.getFluid() == null || tank.getFluidAmount() <= 7000){
            tank.fill(new FluidStack(FluidRegistry.LAVA,1000),true);
            return 1;
        }
        return 0;
    }
}
