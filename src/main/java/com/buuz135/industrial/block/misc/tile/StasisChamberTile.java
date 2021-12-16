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
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.List;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile.WorkAction;

public class StasisChamberTile extends IndustrialAreaWorkingTile<StasisChamberTile> {

    private int getMaxProgress;
    private int getPowerPerOperation;

    public StasisChamberTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<StasisChamberTile>) ModuleMisc.STASIS_CHAMBER.get(), RangeManager.RangeType.TOP, false, StasisChamberConfig.powerPerOperation, blockPos,blockState);
        this.getMaxProgress = StasisChamberConfig.maxProgress;
        this.getPowerPerOperation = StasisChamberConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(getPowerPerOperation)) {
            List<Mob> entities = this.level.getEntitiesOfClass(Mob.class, getWorkingArea().bounds());
            for (Mob entity : entities) {
                entity.setNoAi(true);
                entity.getPersistentData().putLong("StasisChamberTime", this.level.getGameTime());
                if (!entity.canChangeDimensions() && level instanceof ServerLevel) {
                    if (StasisChamberConfig.disableBossBars) {
                        level.players().forEach(entity1 -> entity.stopSeenByPlayer((ServerPlayer) entity1));
                    } else {
                        level.players().forEach(entity1 -> entity.startSeenByPlayer((ServerPlayer) entity1));
                    }
                }
                if (level.random.nextBoolean() && level.random.nextBoolean()) entity.heal(1f);
            }
            List<Player> players = this.level.getEntitiesOfClass(Player.class, getWorkingArea().bounds());
            players.forEach(playerEntity -> {
                playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 135));
                if (level.random.nextBoolean()) playerEntity.heal(1f);
            });
            return new WorkAction(0.5f, getPowerPerOperation);
        }
        return new WorkAction(1, 0);
    }

    @Override
    public VoxelShape getWorkingArea() {
        return Shapes.box(-1, 0, -1, 2, 3, 2).move(this.worldPosition.getX(), this.worldPosition.getY() + 1, this.worldPosition.getZ());
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
