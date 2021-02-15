package com.buuz135.industrial.config.machine.agriculturehusbandry;

import com.buuz135.industrial.config.MachineAgricultureHusbandryConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(MachineAgricultureHusbandryConfig.class)
public class MobDuplicatorConfig {

	@ConfigVal(comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]")
	public static int maxProgress = 62;

	@ConfigVal(comment = "Amount of Power Consumed per Operation - Default: [400FE]")
	public static int powerPerOperation = 5000;

	@ConfigVal(comment = "Max Stored Power [FE] - Default: [10000 FE]")
	public static int maxStoredPower = 50000;

	@ConfigVal(comment = "Max Essence [mb] - Default: [8000 mb]")
	public static int tankSize = 8000;

	@ConfigVal(comment = "Exact Copy to spawn - Default: [false]")
	public static boolean exactCopy = false;

	@ConfigVal(comment = "Essence needed to spawn [Mob health*EssenceNeeded] - Default: [12]")
	public static int essenceNeeded = 12;

}
