package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.SporesRecreatorTile;
import net.minecraft.block.material.Material;

public class SporesRecreatorBlock extends CustomOrientedBlock<SporesRecreatorTile> {

    public SporesRecreatorBlock() {
        super("spores_recreator", SporesRecreatorTile.class, Material.ROCK, 400, 10);
    }
}
