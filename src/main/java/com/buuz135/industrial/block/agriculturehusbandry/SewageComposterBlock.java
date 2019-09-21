package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.agriculturehusbandry.tile.SewageComposterTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class SewageComposterBlock extends BlockRotation<SewageComposterTile> {

    public SewageComposterBlock() {
        super("sewage_composter", Properties.from(Blocks.IRON_BLOCK), SewageComposterTile.class);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<SewageComposterTile> getTileEntityFactory() {
        return () -> new SewageComposterTile();
    }
}
