package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.AnimalByproductRecolectorBlock;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class AnimalByproductRecolectorTile extends WorkingAreaElectricMachine {

    private IFluidTank tank;

    public AnimalByproductRecolectorTile() {
        super(AnimalByproductRecolectorTile.class.getName().hashCode(), 0, 0,true);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.SEWAGE, 8000, EnumDyeColor.BROWN, "Sewage tank", new BoundingRectangle(50, 25, 18, 54));
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(new BlockPos(0, 1, 0)).expand(getRadius(), 0, getRadius());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityAgeable> animals = this.world.getEntitiesWithinAABB(EntityAgeable.class, area);
        int totalFluidAdded = 0;
        for (EntityAgeable animal : animals) {
            int toFill = animal.isChild() ? ((AnimalByproductRecolectorBlock) this.getBlockType()).getSewageBaby() : ((AnimalByproductRecolectorBlock) this.getBlockType()).getSewageAdult();
            tank.fill(new FluidStack(FluidsRegistry.SEWAGE, toFill), true);
            totalFluidAdded += toFill;
            if (totalFluidAdded > ((AnimalByproductRecolectorBlock) this.getBlockType()).getMaxSludgeOperation()) {
                break;
            }
        }

        return 1;
    }
}
