package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.MechanicalDirtTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class MechanicalDirtBlock extends IndustrialBlock<MechanicalDirtTile> {

    public MechanicalDirtBlock() {
        super("mechanical_dirt", Properties.from(Blocks.IRON_BLOCK), MechanicalDirtTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<MechanicalDirtTile> getTileEntityFactory() {
        return MechanicalDirtTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
