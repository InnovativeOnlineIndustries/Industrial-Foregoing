package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.AnimalFeederTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class AnimalFeederBlock extends IndustrialBlock<AnimalFeederTile> {

    public AnimalFeederBlock() {
        super("animal_feeder", Properties.from(Blocks.IRON_BLOCK), AnimalFeederTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<AnimalFeederTile> getTileEntityFactory() {
        return AnimalFeederTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
