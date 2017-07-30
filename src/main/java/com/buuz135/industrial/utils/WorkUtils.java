package com.buuz135.industrial.utils;

import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import net.minecraft.block.Block;

public class WorkUtils {

    public static boolean isDisabled(Block block) {
        return block instanceof CustomOrientedBlock && ((CustomOrientedBlock) block).isWorkDisabled();
    }
}
