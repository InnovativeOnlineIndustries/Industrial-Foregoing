package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SewageComposterTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class SewageComposterBlock extends IndustrialBlock<SewageComposterTile> {

    public SewageComposterBlock() {
        super("sewage_composter", Properties.from(Blocks.IRON_BLOCK), SewageComposterTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
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
