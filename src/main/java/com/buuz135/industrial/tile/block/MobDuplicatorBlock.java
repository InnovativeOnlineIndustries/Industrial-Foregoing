package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.animal.MobDuplicatorTile;
import net.minecraft.block.material.Material;

public class MobDuplicatorBlock extends CustomOrientedBlock<MobDuplicatorTile> {

    public MobDuplicatorBlock() {
        super("mob_duplicator",MobDuplicatorTile.class, Material.ROCK);
    }
}
