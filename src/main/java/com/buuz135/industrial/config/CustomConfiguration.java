package com.buuz135.industrial.config;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;

public class CustomConfiguration {

    public static Configuration config;
    public static HashMap<String, Boolean> configValues;

    public static void sync() {
        try {
            CustomOrientedBlock.blockList.forEach(CustomOrientedBlock::getMachineConfig);

            //sliderBlock.getMachineConfig();
        } catch (Exception e) {

        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
