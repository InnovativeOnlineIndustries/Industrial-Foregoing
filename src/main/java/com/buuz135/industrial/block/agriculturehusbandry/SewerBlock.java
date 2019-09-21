package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.agriculturehusbandry.tile.SewerTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class SewerBlock extends BlockRotation<SewerTile> {

    public SewerBlock() {
        super("sewer", Properties.from(Blocks.IRON_BLOCK), SewerTile.class);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<SewerTile> getTileEntityFactory() {
        return () -> new SewerTile();
    }
}
