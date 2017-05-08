package com.buuz135.industrial.tile.animal;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class MobSlaughterFactoryTile extends WorkingAreaElectricMachine {

    private IFluidTank outMeat;

    public MobSlaughterFactoryTile() {
        super(MobSlaughterFactoryTile.class.getName().hashCode(), 2, 1);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outMeat = this.addFluidTank(FluidsRegistry.MEAT, 8000, EnumDyeColor.BROWN, "Meat tank", new BoundingRectangle(50, 25, 18, 54));
    }

    @Override
    protected int getEnergyForWorkRate() {
        return 20;
    }

    @Override
    protected int getMinimumWorkTicks() {
        return 10;
    }

    @Override
    protected int getEnergyForWork() {
        return 100;
    }

    @Override
    public long getWorkEnergyCapacity() {
        return 100;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1).offset(EnumFacing.UP, getHeight());
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).expand(getRadius(), getHeight(), getRadius()).offset(corner1);
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        if (mobs.size() == 0) return 0;
        EntityLiving mob = mobs.get(this.getWorld().rand.nextInt(mobs.size()));
        this.outMeat.fill(new FluidStack(FluidsRegistry.MEAT, (int) mob.getHealth() * 5), true);
        mob.setDropItemsWhenDead(false);
        mob.attackEntityFrom(CommonProxy.custom, mob.getMaxHealth());
//        List<EntityItem> items = this.getWorld().getEntitiesWithinAABB(EntityItem.class, area);
//        for (EntityItem item : items) {
//            if (!item.getEntityItem().isEmpty()) {
//                item.setDead();
//            }
//        }
        return 1;
    }
}
