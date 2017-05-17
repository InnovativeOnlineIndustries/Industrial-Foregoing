package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.generator.BiofuelGeneratorTile;
import net.minecraft.block.material.Material;

public class BiofuelGeneratorBlock extends CustomOrientedBlock<BiofuelGeneratorTile> {

    public BiofuelGeneratorBlock() {
        super("biofuel_generator", BiofuelGeneratorTile.class, Material.ROCK, 0, 0);
    }
}
