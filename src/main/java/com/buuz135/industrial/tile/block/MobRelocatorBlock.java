package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.agriculture.MobRelocatorTile;
import net.minecraft.block.material.Material;

public class MobRelocatorBlock extends CustomOrientedBlock<MobRelocatorTile> {

    public MobRelocatorBlock() {
        super("mob_relocator", MobRelocatorTile.class, Material.ROCK,1000,40);
    }
}
