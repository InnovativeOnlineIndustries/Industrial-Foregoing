package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class MaterialStoneWorkFactoryConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [60 (3s)]")
    public static int maxProgress = 60;

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [60FE]")
    public static int powerPerTick = 60;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 10000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Water] - Default: [2000mB]")
    public static int maxWaterTankSize = 2000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Lava] - Default: [2000mB]")
    public static int maxLavaTankSize = 2000;

}
