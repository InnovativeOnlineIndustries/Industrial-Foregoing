package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.StasisChamberTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class StasisChamberBlock extends IndustrialBlock<StasisChamberTile> {

    public StasisChamberBlock() {
        super("stasis_chamber", Properties.from(Blocks.IRON_BLOCK), StasisChamberTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<StasisChamberTile> getTileEntityFactory() {
        return StasisChamberTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
