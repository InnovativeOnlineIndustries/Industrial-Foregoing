package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.LaserDrillTile;
import net.minecraft.block.material.Material;

public class LaserDrillBlock extends CustomOrientedBlock<LaserDrillTile> {

    public LaserDrillBlock() {
        super("laser_drill", LaserDrillTile.class, Material.ROCK, 3000, 100);
    }
}
