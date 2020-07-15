package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class SporeRecreatorConfig {

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [40FE]")
    public static int powerPerTick = 40;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 10000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Water] - Default: [8000mB]")
    public static int maxWaterTankSize = 1000;

}
