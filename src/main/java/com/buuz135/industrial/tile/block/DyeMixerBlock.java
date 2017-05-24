package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.misc.DyeMixerTile;
import net.minecraft.block.material.Material;

public class DyeMixerBlock extends CustomOrientedBlock<DyeMixerTile> {

    public DyeMixerBlock() {
        super("dye_mixer", DyeMixerTile.class, Material.ROCK, 100, 10);
    }
}
