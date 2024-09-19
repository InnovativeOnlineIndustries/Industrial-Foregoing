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

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.resourceproduction.LaserDrillConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LaserDrillTile extends IndustrialAreaWorkingTile<LaserDrillTile> {

    @Save
    private BlockPos target;

    public LaserDrillTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.LASER_DRILL, RangeManager.RangeType.BEHIND, false, LaserDrillConfig.powerPerOperation, blockPos, blockState);
        this.target = BlockPos.ZERO;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new TextScreenAddon(ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.target").getString(), 44, 26, false));
        addGuiAddonFactory(() -> new TextScreenAddon("Target: ", 44, 36, false) {
                    @Override
                    public String getText() {
                        if (target.equals(BlockPos.ZERO) || target == null)
                            return ChatFormatting.DARK_GRAY + Component.translatable("text.industrialforegoing.target_not_found").getString();
                        return ChatFormatting.DARK_GRAY + "X: " + target.getX() + " Y: " + target.getY() + " Z: " + target.getZ();
                    }
                }
        );
    }

    @Override
    public WorkAction work() {
        if (!target.equals(BlockPos.ZERO) && !isValidTarget(target)) {
            target = BlockPos.ZERO;
            markForUpdate();
        }
        if (target.equals(BlockPos.ZERO)) {
            findTarget();
        }
        if (!target.equals(BlockPos.ZERO) && hasEnergy(LaserDrillConfig.powerPerOperation)) {
            if (this.level.getBlockEntity(target) instanceof ILaserBase) {
                ILaserBase laserBase = ((ILaserBase<?>) this.level.getBlockEntity(target));
                laserBase.getBar().setProgress(laserBase.getBar().getProgress() + 1);
                laserBase.getBar().tickBar();
                return new WorkAction(1, LaserDrillConfig.powerPerOperation);
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    public VoxelShape getWorkingArea() {
        Vec3i vector = this.getFacingDirection().getOpposite().getNormal();
        return Shapes.box(-1, 0, -1, 2, 3, 2).move(this.worldPosition.getX() + vector.getX() * 2, this.worldPosition.getY() + vector.getY() * 2 - 1, this.worldPosition.getZ() + vector.getZ() * 2);
    }

    public void findTarget() {
        for (BlockPos blockPos : BlockUtils.getBlockPosInAABB(getWorkingArea().bounds())) {
            if (isValidTarget(blockPos)) {
                this.target = blockPos;
                markForUpdate();
                return;
            }
        }
    }

    public boolean isValidTarget(BlockPos pos) {
        if (pos.equals(BlockPos.ZERO)) return false;
        return this.level.getBlockEntity(pos) instanceof ILaserBase;
    }

    @Override
    public LaserDrillTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<LaserDrillTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(LaserDrillConfig.maxStoredPower, 10, 20);
    }

    public BlockPos getTarget() {
        return target;
    }

    @Override
    public int getMaxProgress() {
        return LaserDrillConfig.maxProgress;
    }
}
