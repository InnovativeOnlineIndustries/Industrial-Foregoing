package com.buuz135.industrial.capability.tile;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;

import java.util.Collections;
import java.util.List;

public abstract class BigSidedFluidTankComponent<T extends IComponentHarness> extends SidedFluidTankComponent<T> {

    public BigSidedFluidTankComponent(String name, int amount, int posX, int posY, int pos) {
        super(name, amount, posX, posY, pos);
    }


    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    protected void onContentsChanged() {
        sync();
    }

    public abstract void sync();
}
