package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.BlockPlacerTile;
import net.minecraft.block.material.Material;

public class BlockPlacerBlock extends CustomOrientedBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", BlockPlacerTile.class, Material.ROCK);
    }
}
