package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SlaughterFactoryTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class SlaughterFactoryBlock extends IndustrialBlock<SlaughterFactoryTile> {

    public SlaughterFactoryBlock() {
        super("mob_slaughter_factory", Properties.from(Blocks.IRON_BLOCK), SlaughterFactoryTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<SlaughterFactoryTile> getTileEntityFactory() {
        return SlaughterFactoryTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
