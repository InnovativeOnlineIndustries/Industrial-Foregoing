package com.buuz135.industrial.config;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;

public class CustomConfiguration {

    public static Configuration config;
    public static HashMap<String, Boolean> configValues;
    public static boolean enableBookEntriesInJEI;

    public static void sync() {
        try {
            CustomOrientedBlock.blockList.forEach(CustomOrientedBlock::getMachineConfig);
            enableBookEntriesInJEI = config.getBoolean("enableBookEntriesInJEI", Configuration.CATEGORY_CLIENT, true, "Enable to show book entries in JEI");
        } catch (Exception e) {

        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
