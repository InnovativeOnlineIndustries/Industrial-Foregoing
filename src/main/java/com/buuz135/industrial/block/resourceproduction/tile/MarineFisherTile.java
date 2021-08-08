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
import com.buuz135.industrial.config.machine.resourceproduction.MarineFisherConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.BlockUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class MarineFisherTile extends IndustrialAreaWorkingTile<MarineFisherTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedInventoryComponent<MarineFisherTile> output;

    public MarineFisherTile() {
        super(ModuleResourceProduction.MARINE_FISHER, RangeManager.RangeType.BOTTOM, false, MarineFisherConfig.powerPerOperation);
        addInventory(output = (SidedInventoryComponent<MarineFisherTile>) new SidedInventoryComponent<MarineFisherTile>("output", 50, 22, 3 * 6, 0)
                .setColor(DyeColor.ORANGE)
                .setRange(6, 3)
                .setComponentHarness(this));
        this.maxProgress = MarineFisherConfig.maxProgress;
        this.powerPerOperation = MarineFisherConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            if (getWaterSources() < 9) return new WorkAction(1, 0);
            LootTable fishingTable = this.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
            if (this.world.rand.nextDouble() <= 0.02) {
                fishingTable = this.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING_TREASURE);
            }
            LootContext.Builder context = new LootContext.Builder((ServerWorld) this.world).withParameter(LootParameters.field_237457_g_, new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ())).withParameter(LootParameters.TOOL, new ItemStack(Items.FISHING_ROD));
            fishingTable.generate(context.build(LootParameterSets.FISHING)).forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
            return new WorkAction(1f, powerPerOperation);
        }
        return new WorkAction(1f, 0);
    }

    @Override
    protected EnergyStorageComponent<MarineFisherTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(MarineFisherConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public MarineFisherTile getSelf() {
        return this;
    }

    public VoxelShape getWorkingArea() {
        return new RangeManager(this.pos, this.getFacingDirection(), RangeManager.RangeType.BOTTOM).get(1);
    }

    private int getWaterSources() {
        int amount = 0;
        for (BlockPos pos : BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox())) {
            if (!world.isAreaLoaded(pos, pos)) continue;
            FluidState fluidState = this.world.getFluidState(pos);
            if (fluidState.getFluid().equals(Fluids.WATER) && fluidState.isSource()) {
                ++amount;
            }
        }
        return amount;
    }

}
