package com.buuz135.industrial.proxy.client.model;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.utils.PropertyUnlisted;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class ConveyorModelData {

    public static final PropertyUnlisted<ConveyorModelData> UPGRADE_PROPERTY = PropertyUnlisted.create("upgrade", ConveyorModelData.class);
    private Map<EnumFacing, ConveyorUpgrade> upgrades;

    public Map<EnumFacing, ConveyorUpgrade> getUpgrades() {
        return upgrades;
    }

    public ConveyorModelData(Map<EnumFacing, ConveyorUpgrade> upgrades) {
        this.upgrades = upgrades;
    }
}
