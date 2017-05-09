package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.BlockDestroyerTile;
import net.minecraft.block.material.Material;

public class BlockDestroyerBlock extends CustomOrientedBlock<BlockDestroyerTile> {

    public BlockDestroyerBlock() {
        super("block_destroyer", BlockDestroyerTile.class, Material.ROCK, 100,20);
    }
}
