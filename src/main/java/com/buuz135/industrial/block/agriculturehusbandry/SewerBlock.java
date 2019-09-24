package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SewerTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class SewerBlock extends IndustrialBlock<SewerTile> {

    public SewerBlock() {
        super("sewer", Properties.from(Blocks.IRON_BLOCK), SewerTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
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
