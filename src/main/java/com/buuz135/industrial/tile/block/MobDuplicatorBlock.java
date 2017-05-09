package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.MobDuplicatorTile;
import net.minecraft.block.material.Material;

public class MobDuplicatorBlock extends CustomOrientedBlock<MobDuplicatorTile> {

    public MobDuplicatorBlock() {
        super("mob_duplicator", MobDuplicatorTile.class, Material.ROCK,5000,80);
    }
}
