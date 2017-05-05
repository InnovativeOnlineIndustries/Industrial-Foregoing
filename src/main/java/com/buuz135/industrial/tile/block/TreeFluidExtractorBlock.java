package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.TreeFluidExtractorTile;
import net.minecraft.block.material.Material;

public class TreeFluidExtractorBlock extends CustomOrientedBlock<TreeFluidExtractorTile> {

    public TreeFluidExtractorBlock() {
        super("tree_fluid_extractor",TreeFluidExtractorTile.class,Material.ROCK);
    }
}
