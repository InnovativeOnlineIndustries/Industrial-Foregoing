package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineCoreConfig;
import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class SporeRecreatorConfig {

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [400FE]")
    public static int getPowerPerTick = 1000;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int getMaxStoredPower = 10000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Water] - Default: [8000mB]")
    public static int getMaxWaterTankSize = 1000;

}
