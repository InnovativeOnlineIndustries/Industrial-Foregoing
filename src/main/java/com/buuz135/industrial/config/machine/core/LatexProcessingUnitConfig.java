package com.buuz135.industrial.config.machine.core;

import com.buuz135.industrial.config.MachineCoreConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineCoreConfig.class)
public class LatexProcessingUnitConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]")
    public static int getMaxProgress = 100;

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [400FE]")
    public static int getPowerPerTick = 20;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int getMaxStoredPower = 10000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Latex] - Default: [8000mB]")
    public static int getMaxLatexTankSize = 16000;

    @ConfigVal(comment = "Max Amount of Stored Fluid [Water] - Default: [8000mB]")
    public static int getMaxWaterTankSize = 16000;

}
