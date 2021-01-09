/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.misc.StasisChamberConfig;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
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
        super(ModuleMisc.STASIS_CHAMBER, RangeManager.RangeType.TOP, false, StasisChamberConfig.powerPerOperation);
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
    protected EnergyStorageComponent<StasisChamberTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(StasisChamberConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return getMaxProgress;
    }
}
