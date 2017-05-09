package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.world.LatexProcessingUnitTile;
import net.minecraft.block.material.Material;

public class LatexProcessingUnitBlock extends CustomOrientedBlock<LatexProcessingUnitTile> {

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", LatexProcessingUnitTile.class, Material.ROCK,200,5);
    }
}
