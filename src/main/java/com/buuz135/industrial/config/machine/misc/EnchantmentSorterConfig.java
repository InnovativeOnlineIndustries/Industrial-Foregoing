package com.buuz135.industrial.config.machine.misc;

import com.buuz135.industrial.config.MachineMiscConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineMiscConfig.class)
public class EnchantmentSorterConfig {

    @ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [50 (2.5s)]")
    public static int maxProgress = 50;

    @ConfigVal(comment = "Amount of Power Consumed per Tick - Default: [40FE]")
    public static int powerPerTick = 40;

    @ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
    public static int maxStoredPower = 10000;


}
