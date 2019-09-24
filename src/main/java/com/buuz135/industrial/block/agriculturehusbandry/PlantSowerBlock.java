package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantSowerTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class PlantSowerBlock extends IndustrialBlock<PlantSowerTile> {

    public PlantSowerBlock() {
        super("plant_sower", Properties.from(Blocks.IRON_BLOCK), PlantSowerTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<PlantSowerTile> getTileEntityFactory() {
        return PlantSowerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
