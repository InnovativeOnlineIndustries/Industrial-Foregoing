package com.buuz135.industrial.config;

import java.util.HashMap;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;

import net.minecraftforge.common.config.Configuration;

public class CustomConfiguration {

	public static Configuration config;
	public static HashMap<String, Boolean> configValues;
	public static boolean enableBookEntriesInJEI;
	public static boolean enableAdditionalLens;

	public static void sync() {
		try {
			CustomOrientedBlock.blockList.forEach(CustomOrientedBlock::getMachineConfig);
			enableBookEntriesInJEI = config.getBoolean("enableBookEntriesInJEI", Configuration.CATEGORY_CLIENT, true, "Enable to show book entries in JEI");
			enableAdditionalLens = config.getBoolean("additionalLens", Configuration.CATEGORY_GENERAL, true, "Add coated lens for better laser control");
		} catch (Exception e) {

		} finally {
			if (config.hasChanged()) config.save();
		}
	}
}
