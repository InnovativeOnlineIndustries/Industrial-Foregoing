package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialMachineTile;
import com.buuz135.industrial.item.infinity.InfinityEnergyStorage;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraftforge.energy.CapabilityEnergy;

public class InfinityChargerTile extends IndustrialMachineTile<InfinityChargerTile> {

    @Save
    private SidedInventoryComponent<InfinityChargerTile> chargingSlot;

    public InfinityChargerTile() {
        super(ModuleMisc.INFINITY_CHARGER);
        addInventory(chargingSlot = (SidedInventoryComponent<InfinityChargerTile>) new SidedInventoryComponent<InfinityChargerTile>("charging", 80, 40, 1, 0)
                .setColor(DyeColor.BLUE)
                .setSlotLimit(1)
                .setInputFilter((stack, integer) -> stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
        );
    }

    @Override
    public void tick() {
        super.tick();
        if (isServer()){
            if (!chargingSlot.getStackInSlot(0).isEmpty()){
                chargingSlot.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> {
                    if (iEnergyStorage instanceof InfinityEnergyStorage && this.getEnergyStorage() instanceof InfinityEnergyStorage){
                        long added = Math.min(Long.MAX_VALUE - ((InfinityEnergyStorage) iEnergyStorage).getLongEnergyStored() , Math.min(((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongCapacity(), ((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongEnergyStored()));
                        ((InfinityEnergyStorage) iEnergyStorage).setEnergyStored(((InfinityEnergyStorage) iEnergyStorage).getLongEnergyStored() + added);
                        ((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).setEnergyStored(((InfinityEnergyStorage<InfinityChargerTile>) this.getEnergyStorage()).getLongEnergyStored() - added);
                        markForUpdate();
                    } else {
                        int extracted = this.getEnergyStorage().extractEnergy(Integer.MAX_VALUE, true);
                        this.getEnergyStorage().extractEnergy(iEnergyStorage.receiveEnergy(extracted, false), false);
                        markForUpdate();
                    }
                });
            }
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
