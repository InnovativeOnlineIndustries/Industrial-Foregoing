package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class SimulatedHydroponicBedConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [200 (5s)]")
    public static int maxProgress = 200;

    @ConfigVal(comment = "Amount of Power Consumed per Operation - Default: [3000FE]")
    public static int powerPerOperation = 3000;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [20000 FE]")
    public static int maxStoredPower = 20000;

}
