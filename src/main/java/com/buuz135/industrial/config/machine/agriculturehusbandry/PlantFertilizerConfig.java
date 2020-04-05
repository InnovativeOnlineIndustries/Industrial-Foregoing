package com.buuz135.industrial.config.machine.agriculturehusbandry;

import com.buuz135.industrial.config.MachineAgricultureHusbandryConfig;
import com.buuz135.industrial.config.MachineCoreConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineAgricultureHusbandryConfig.class)
public class PlantFertilizerConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]")
    public static int getMaxProgress = 50;

    @ConfigVal(comment = "Amount of Power Consumer per Operation - Default: [400FE]")
    public static int getPowerPerOperation = 1000;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int getMaxStoredPower = 10000;

}
