package com.buuz135.industrial.block.mob.tile;

import com.buuz135.industrial.block.mob.MobDetectorBlock;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleMob;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class MobDetectorTile extends IndustrialAreaWorkingTile<MobDetectorTile> {

    public MobDetectorTile() {
        super(ModuleMob.MOB_DETECTOR, RangeManager.RangeType.FRONT);
    }

    @Override
    public WorkAction work() {
        if (this.world != null && this.world.getBlockState(pos).getBlock() instanceof MobDetectorBlock) {
            MobDetectorBlock detector = (MobDetectorBlock) this.world.getBlockState(pos).getBlock();
            List<LivingEntity> living = this.world.getEntitiesWithinAABB(LivingEntity.class, getWorkingArea().getBoundingBox());
            detector.setRedstoneSignal(Math.min(living.size(), 15));
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBasicTileBlock());
        }
        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return 20;
    }

    @Nonnull
    @Override
    public MobDetectorTile getSelf() {
        return this;
    }

}
