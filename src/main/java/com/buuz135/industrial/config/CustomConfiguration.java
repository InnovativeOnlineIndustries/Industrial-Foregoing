/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.config;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;

public class CustomConfiguration {

    public static Configuration config;
    public static HashMap<String, Boolean> configValues;
    public static boolean enableBookEntriesInJEI;
    public static boolean enableMultiblockEdition;

    public static void sync() {
        try {
            enableBookEntriesInJEI = config.getBoolean("enableBookEntriesInJEI", Configuration.CATEGORY_GENERAL, true, "Enable to show book entries in JEI");
            enableMultiblockEdition = config.getBoolean("enableMultiblockEdition", Configuration.CATEGORY_GENERAL, true, "Enable to allow the multiblock edition to be used");
            CustomOrientedBlock.blockList.forEach(CustomOrientedBlock::getMachineConfig);
            ItemRegistry.itemInfinityDrill.configuration(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (config.hasChanged()) config.save();
    }
}
