package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.misc.MobDetectorBlock;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.List;

public class MobDetectorTile extends IndustrialAreaWorkingTile<MobDetectorTile> {

    private int redstoneSignal;

    public MobDetectorTile() {
        super(ModuleMisc.MOB_DETECTOR, RangeManager.RangeType.BEHIND, true);
        this.redstoneSignal = 0;
    }

    @Override
    public WorkAction work() {
        if (this.world != null && this.world.getBlockState(pos).getBlock() instanceof MobDetectorBlock) {
            List<LivingEntity> living = this.world.getEntitiesWithinAABB(LivingEntity.class, getWorkingArea().getBoundingBox());
            redstoneSignal = Math.min(living.size(), 15);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBasicTileBlock());
        }
        return new WorkAction(1, 0);
    }

    @Override
    public int getMaxProgress() {
        return 10;
    }

    @Nonnull
    @Override
    public MobDetectorTile getSelf() {
        return this;
    }

    public int getRedstoneSignal() {
        return redstoneSignal;
    }

    @Override
    protected EnergyStorageComponent<MobDetectorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(1, 10, 20);
    }

}
