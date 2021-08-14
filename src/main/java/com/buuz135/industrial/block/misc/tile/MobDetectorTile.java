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
        super(ModuleMisc.MOB_DETECTOR, RangeManager.RangeType.BEHIND, true, 0);
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
