package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.gui.component.InfinityEnergyScreenAddon;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class InfinityEnergyStorage implements IEnergyStorage, IScreenAddonProvider {

    private final long capacity;
    private long energy;

    public InfinityEnergyStorage() {
        this.energy = 0;
        this.capacity = InfinityTier.ARTIFACT.getPowerNeeded();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
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
        return (int) energy;
    }

    public void setEnergyStored(long power) {
        this.energy = power;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) capacity;
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

    @Nonnull
    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(() -> new InfinityEnergyScreenAddon(10, 20, this));
    }
}
