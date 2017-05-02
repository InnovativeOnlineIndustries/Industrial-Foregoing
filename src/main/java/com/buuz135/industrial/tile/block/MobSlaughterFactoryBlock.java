package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.animal.MobSlaughterFactoryTile;
import net.minecraft.block.material.Material;

public class MobSlaughterFactoryBlock extends CustomOrientedBlock<MobSlaughterFactoryTile> {

    public MobSlaughterFactoryBlock() {
        super("mob_slaughter_factory", MobSlaughterFactoryTile.class, Material.ROCK);
    }
}
