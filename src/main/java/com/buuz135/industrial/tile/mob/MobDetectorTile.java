package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.entity.EntityLiving;

import java.util.List;

public class MobDetectorTile extends WorkingAreaElectricMachine {

    private int redstoneSignal;

    public MobDetectorTile() {
        super(MobDetectorTile.class.getName().hashCode());
        redstoneSignal = 0;
    }

    @Override
    public float work() {
        List<EntityLiving> living = this.world.getEntitiesWithinAABB(EntityLiving.class, getWorkingArea());
        redstoneSignal = living.size() > 15 ? 15 : living.size();
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
        return 1;
    }

    public int getRedstoneSignal() {
        return redstoneSignal;
    }
}
