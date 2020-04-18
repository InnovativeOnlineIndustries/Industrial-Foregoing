package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.misc.StasisChamberConfig;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nonnull;
import java.util.List;

public class StasisChamberTile extends IndustrialAreaWorkingTile<StasisChamberTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    public StasisChamberTile() {
        super(ModuleMisc.STASIS_CHAMBER, RangeManager.RangeType.TOP);
        this.getMaxProgress = StasisChamberConfig.maxProgress;
        this.getPowerPerOperation = StasisChamberConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            List<MobEntity> entities = this.world.getEntitiesWithinAABB(MobEntity.class, getWorkingArea().getBoundingBox());
            for (MobEntity entity : entities) {
                entity.setNoAI(true);
                entity.getPersistentData().putLong("StasisChamberTime", this.world.getGameTime());
                if (world.rand.nextBoolean() && world.rand.nextBoolean()) entity.heal(1f);
            }
            List<PlayerEntity> players = this.world.getEntitiesWithinAABB(PlayerEntity.class, getWorkingArea().getBoundingBox());
            players.forEach(playerEntity -> {
                playerEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 25, 135));
                if (world.rand.nextBoolean()) playerEntity.heal(1f);
            });
            return new WorkAction(0.5f, getPowerPerOperation);
        }
        return new WorkAction(1, 0);
    }

    @Override
    public VoxelShape getWorkingArea() {
        return VoxelShapes.create(-1, 0, -1, 2, 3, 2).withOffset(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ());
    }

    @Nonnull
    @Override
    public StasisChamberTile getSelf() {
        return this;
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, StasisChamberConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }
}
