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

package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile;
import com.buuz135.industrial.config.machine.resourceproduction.MechanicalDirtConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class MechanicalDirtTile extends IndustrialWorkingTile<MechanicalDirtTile> {

    private int getPowerPerOperation;

    @Save
    private SidedFluidTankComponent<MechanicalDirtTile> meat;

    public MechanicalDirtTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.MECHANICAL_DIRT, MechanicalDirtConfig.powerPerOperation, blockPos, blockState);
        addTank(meat = (SidedFluidTankComponent<MechanicalDirtTile>) new SidedFluidTankComponent<MechanicalDirtTile>("meat", MechanicalDirtConfig.maxMeatTankSize, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setComponentHarness(this).
                setTankAction(FluidTankComponent.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isSame(ModuleCore.MEAT.getSourceFluid().get()))
        );
        this.getPowerPerOperation = MechanicalDirtConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (level.random.nextDouble() > 0.1
                || level.getDifficulty() == Difficulty.PEACEFUL
                || (level.isDay() && level.getBrightness(LightLayer.SKY, worldPosition.above()) > 0.5f && level.canSeeSkyFromBelowWater(worldPosition.above()))
                || level.getEntitiesOfClass(Mob.class, new AABB(0, 0, 0, 1, 1, 1).move(worldPosition).inflate(3)).size() > 10
                || level.getMaxLocalRawBrightness(worldPosition.above()) > 7) {
            if (hasEnergy(getPowerPerOperation / 10)) return new WorkAction(0.5f, getPowerPerOperation / 10);
            return new WorkAction(1, 0);
        }
        if (meat.getFluidAmount() >= 20 && isServer()) {
            Mob entity = getMobToSpawn();
            if (entity != null) {
                level.addFreshEntity(entity);
                meat.drainForced(20, IFluidHandler.FluidAction.EXECUTE);
                if (hasEnergy(getPowerPerOperation)) return new WorkAction(0.5f, getPowerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    private Mob getMobToSpawn() {
        List<MobSpawnSettings.SpawnerData> spawnListEntries = ((ServerLevel) this.level).getChunkSource().getGenerator().getMobsAt(this.level.getBiome(this.worldPosition.above()), ((ServerLevel) this.level).structureManager(), MobCategory.MONSTER, this.worldPosition.above()).unwrap();
        if (spawnListEntries.isEmpty()) return null;
        MobSpawnSettings.SpawnerData spawnListEntry = spawnListEntries.get(level.random.nextInt(spawnListEntries.size()));
        if (!SpawnPlacements.checkSpawnRules(spawnListEntry.type, (ServerLevelAccessor) this.level, MobSpawnType.NATURAL, worldPosition.above(), level.random))
            return null;
        Entity entity = spawnListEntry.type.create(level);
        if (entity instanceof Mob) {
            EventHooks.finalizeMobSpawn((Mob) entity, (ServerLevelAccessor) level, level.getCurrentDifficultyAt(worldPosition), MobSpawnType.NATURAL, null);
            entity.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.001, worldPosition.getZ() + 0.5);
            if (level.noCollision(entity) && level.isUnobstructed(entity, level.getBlockState(worldPosition.above()).getShape(level, worldPosition.above()))) { //doesNotCollide
                return (Mob) entity;
            }
        }
        return null;
    }

    public SidedFluidTankComponent<MechanicalDirtTile> getMeat() {
        return meat;
    }


    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, MechanicalDirtTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        if (this.level.getGameTime() % 5 == 0) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockEntity tile = level.getBlockEntity(worldPosition.relative(direction));
                if (tile instanceof MechanicalDirtTile) {
                    int difference = meat.getFluidAmount() - ((MechanicalDirtTile) tile).getMeat().getFluidAmount();
                    if (difference > 0) {
                        if (difference <= 25) difference = difference / 2;
                        else difference = 25;
                        if (meat.getFluidAmount() >= difference) {
                            meat.drainForced(((MechanicalDirtTile) tile).getMeat().fill(new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), meat.drainForced(difference, IFluidHandler.FluidAction.SIMULATE).getAmount()), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                    difference = getEnergyStorage().getEnergyStored() - ((MechanicalDirtTile) tile).getEnergyStorage().getEnergyStored();
                    if (difference > 0) {
                        if (difference <= 1000 && difference > 1) difference = difference / 2;
                        if (difference > 1000) difference = 1000;
                        if (getEnergyStorage().getEnergyStored() >= difference) {
                            getEnergyStorage().extractEnergy(((MechanicalDirtTile) tile).getEnergyStorage().receiveEnergy(difference, false), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected EnergyStorageComponent<MechanicalDirtTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(MechanicalDirtConfig.maxStoredPower, 10, 20);
    }

    @Nonnull
    @Override
    public MechanicalDirtTile getSelf() {
        return this;
    }

    @Override
    public int getMaxProgress() {
        return MechanicalDirtConfig.maxProgress;
    }
}
