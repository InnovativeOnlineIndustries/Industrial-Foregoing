package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class FluidExtractorBlock extends BlockRotation<FluidExtractorTile> {

    public FluidExtractorBlock() {
        super("fluid_extractor", Properties.from(Blocks.STONE), FluidExtractorTile.class);
    }

    @Override
    public IFactory<FluidExtractorTile> getTileEntityFactory() {
        return FluidExtractorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
