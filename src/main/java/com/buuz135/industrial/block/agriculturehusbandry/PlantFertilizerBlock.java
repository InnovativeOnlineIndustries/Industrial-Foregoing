package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantFertilizerTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class PlantFertilizerBlock extends IndustrialBlock<PlantFertilizerTile> {

    public PlantFertilizerBlock() {
        super("plant_fertilizer", Properties.from(Blocks.IRON_BLOCK), PlantFertilizerTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<PlantFertilizerTile> getTileEntityFactory() {
        return PlantFertilizerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
