package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.LatexProcessingUnitTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class LatexProcessingUnitBlock extends BlockRotation<LatexProcessingUnitTile> {

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", Properties.from(Blocks.STONE), LatexProcessingUnitTile.class);
    }

    @Override
    public IFactory<LatexProcessingUnitTile> getTileEntityFactory() {
        return LatexProcessingUnitTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
