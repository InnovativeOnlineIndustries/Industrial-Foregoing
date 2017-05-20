package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class MobDetectorTile extends WorkingAreaElectricMachine {

    private int redstoneSignal;

    public MobDetectorTile() {
        super(MobDetectorTile.class.getName().hashCode(), 2, 1, false);
        redstoneSignal = 0;
    }


    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).expand(getRadius(), 0, getRadius()).setMaxY(this.getPos().getY() + getHeight());
    }

    @Override
    protected float performWork() {
        List<EntityLiving> living = this.world.getEntitiesWithinAABB(EntityLiving.class, getWorkingArea());
        redstoneSignal = living.size() > 15 ? 15 : living.size();
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
        return 1;
    }

    public int getRedstoneSignal() {
        return redstoneSignal;
    }
}
