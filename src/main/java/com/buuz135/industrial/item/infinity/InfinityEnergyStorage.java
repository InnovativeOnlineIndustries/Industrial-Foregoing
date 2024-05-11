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

package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.gui.component.InfinityEnergyScreenAddon;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class InfinityEnergyStorage<T extends IComponentHarness> extends EnergyStorageComponent<T> {

    private final long capacity;
    private long energy;

    public InfinityEnergyStorage(long capacity, int xPos, int yPos) {
        super(Integer.MAX_VALUE, xPos, yPos);
        this.energy = 0;
        this.capacity = capacity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        long stored = getLongEnergyStored();
        int energyReceived = (int) Math.min(capacity - stored, Math.min(Long.MAX_VALUE, maxReceive));
        if (!simulate)
            setEnergyStored(stored + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) this.energy;
    }

    public void setEnergyStored(long power) {
        this.energy = power;
        if (this.componentHarness != null) {
            this.componentHarness.markComponentForUpdate(false);
        }
    }

    @Override
    public int getMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    public long getLongEnergyStored() {
        return this.energy;
    }

    public long getLongCapacity() {
        return capacity;
    }

    @Override
    @Nonnull
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("energy", this.energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getLong("energy");
    }


    @Nonnull
    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(() -> new InfinityEnergyScreenAddon(getX(), getY(), this));
    }
}
