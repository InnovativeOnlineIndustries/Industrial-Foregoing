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

import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;

public class InfinityChargerTile extends IndustrialMachineTile<InfinityChargerTile> {

    @Save
    private SidedInventoryComponent<InfinityChargerTile> chargingSlot;

    public InfinityChargerTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<InfinityChargerTile>) ModuleMisc.INFINITY_CHARGER.get(),blockPos, blockState);
        addInventory(chargingSlot = (SidedInventoryComponent<InfinityChargerTile>) new SidedInventoryComponent<InfinityChargerTile>("charging", 80, 40, 1, 0)
                .setColor(DyeColor.BLUE)
                .setSlotLimit(1)
                .setInputFilter((stack, integer) -> stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
        );
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, InfinityChargerTile blockEntity) {
        if (!chargingSlot.getStackInSlot(0).isEmpty()) {
            chargingSlot.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> {
                if (this.getEnergyStorage() instanceof InfinityEnergyStorage) {
                    if (iEnergyStorage instanceof InfinityEnergyStorage) {
                        long added = Math.min(Long.MAX_VALUE - ((InfinityEnergyStorage) iEnergyStorage).getLongEnergyStored(), Math.min(((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongCapacity(), ((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongEnergyStored()));
                        ((InfinityEnergyStorage) iEnergyStorage).setEnergyStored(((InfinityEnergyStorage) iEnergyStorage).getLongEnergyStored() + added);
                        ((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).setEnergyStored(((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongEnergyStored() - added);
                        markForUpdate();
                    } else {
                        int extracted = this.getEnergyStorage().getEnergyStored();
                        ((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).setEnergyStored(((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongEnergyStored() - iEnergyStorage.receiveEnergy(extracted, false));
                        markForUpdate();
                    }
                }
            });
        }
    }

    @Override
    public InfinityChargerTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<InfinityChargerTile> createEnergyStorage() {
        return new InfinityEnergyStorage<>(1_000_000_000_000L, 10, 20);
    }
}
