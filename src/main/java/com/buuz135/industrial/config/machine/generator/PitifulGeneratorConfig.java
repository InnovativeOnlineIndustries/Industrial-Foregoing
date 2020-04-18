package com.buuz135.industrial.config.machine.generator;

import com.buuz135.industrial.config.MachineGeneratorConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineGeneratorConfig.class)
public class PitifulGeneratorConfig {

    @ConfigVal(comment = "Amount of Power Produced per Tick - Default: [400FE]")
    public static int powerPerTick = 30;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 100000;

    @ConfigVal(comment = "Amount of FE/t extracted from the Pitiful Generator")
    public static int extractionRate = 1000;

}
