package com.buuz135.industrial.capability.tile;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public abstract class BigEnergyHandler<T extends IComponentHarness> extends EnergyStorageComponent<T> {

    public BigEnergyHandler(int maxCapacity, int xPos, int yPos) {
        super(maxCapacity, xPos, yPos);
    }

    public BigEnergyHandler(int maxCapacity, int maxIO, int xPos, int yPos) {
        super(maxCapacity, maxIO, xPos, yPos);
    }

    public BigEnergyHandler(int maxCapacity, int maxReceive, int maxExtract, int xPos, int yPos) {
        super(maxCapacity, maxReceive, maxExtract, xPos, yPos);
    }

    @Nonnull
    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int amount = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && amount > 0) {
            this.sync();
        }
        return amount;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int amount = super.extractEnergy(maxExtract, simulate);
        if (!simulate && amount > 0) {
            this.sync();
        }
        return amount;
    }

    public void setEnergyStored(int energy) {
        if (energy > this.getMaxEnergyStored()) {
            this.energy = this.getMaxEnergyStored();
        } else {
            this.energy = Math.max(energy, 0);
        }
        this.sync();
    }

    public abstract void sync();

}
