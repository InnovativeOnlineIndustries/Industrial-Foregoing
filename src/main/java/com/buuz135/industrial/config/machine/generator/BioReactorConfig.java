package com.buuz135.industrial.config.machine.generator;

import com.buuz135.industrial.config.MachineCoreConfig;
import com.buuz135.industrial.config.MachineGeneratorConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineGeneratorConfig.class)
public class BioReactorConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]")
    public static int getMaxProgress = 100;

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [400FE]")
    public static int getPowerPerOperation = 400;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int getMaxStoredPower = 10000;

    @ConfigVal
    public static int getMaxWaterTankStorage = 16000;

    @ConfigVal
    public static int getMaxBioFuelTankStorage = 16000;
}
