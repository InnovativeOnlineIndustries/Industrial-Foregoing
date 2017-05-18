package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.misc.OreProcessorTile;
import net.minecraft.block.material.Material;

public class OreProcessorBlock extends CustomOrientedBlock<OreProcessorTile> {

    public OreProcessorBlock() {
        super("ore_processor", OreProcessorTile.class, Material.ROCK, 1000,40);
    }
}
