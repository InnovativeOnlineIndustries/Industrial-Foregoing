package com.buuz135.industrial.config.machine.resourceproduction;

import com.buuz135.industrial.config.MachineResourceProductionConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineResourceProductionConfig.class)
public class OreLaserBaseConfig {

    @ConfigVal(comment = "Max progress of the machine")
    public static int maxProgress = 100;

    @ConfigVal(comment = "How much weight of an item the catalyst will increase")
    public static int catalystModifier = 8;

}
