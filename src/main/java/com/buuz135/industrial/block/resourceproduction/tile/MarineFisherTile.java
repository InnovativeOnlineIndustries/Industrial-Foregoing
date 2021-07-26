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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

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
            LootTable fishingTable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
            if (this.level.random.nextDouble() <= 0.05) {
                fishingTable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_TREASURE);
            }
            LootContext.Builder context = new LootContext.Builder((ServerLevel) this.level).withParameter(LootContextParams.ORIGIN, new Vec3(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ())).withParameter(LootContextParams.TOOL, new ItemStack(Items.FISHING_ROD));
            fishingTable.getRandomItems(context.create(LootContextParamSets.FISHING)).forEach(stack -> ItemHandlerHelper.insertItem(output, stack, false));
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
        return new RangeManager(this.worldPosition, this.getFacingDirection(), RangeManager.RangeType.BOTTOM).get(1);
    }

    private int getWaterSources() {
        int amount = 0;
        for (BlockPos pos : BlockUtils.getBlockPosInAABB(getWorkingArea().bounds())) {
            if (!level.hasChunksAt(pos, pos)) continue;
            FluidState fluidState = this.level.getFluidState(pos);
            if (fluidState.getType().equals(Fluids.WATER) && fluidState.isSource()) {
                ++amount;
            }
        }
        return amount;
    }

}
