package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.misc.WaterCondesatorTile;
import net.minecraft.block.material.Material;

public class WaterCondensatorBlock extends CustomOrientedBlock<WaterCondesatorTile> {


    public WaterCondensatorBlock() {
        super("water_condensator", WaterCondesatorTile.class, Material.ROCK,0,0);
    }
}
