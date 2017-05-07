package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.generator.PetrifiedFuelGeneratorTile;
import net.minecraft.block.material.Material;

public class PetrifiedFuelGeneratorBlock extends CustomOrientedBlock<PetrifiedFuelGeneratorTile> {

    public PetrifiedFuelGeneratorBlock() {
        super("petrified_fuel_generator", PetrifiedFuelGeneratorTile.class, Material.ROCK);
    }


}
