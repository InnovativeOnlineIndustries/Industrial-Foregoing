package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class DissolutionChamberBlock extends BlockRotation<DissolutionChamberTile> {

    public DissolutionChamberBlock() {
        super("dissolution_chamber", Properties.from(Blocks.IRON_BLOCK), DissolutionChamberTile.class);
    }

    @Override
    public IFactory<DissolutionChamberTile> getTileEntityFactory() {
        return DissolutionChamberTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
