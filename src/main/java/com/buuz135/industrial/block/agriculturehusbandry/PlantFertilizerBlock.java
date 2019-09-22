package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantFertilizerTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class PlantFertilizerBlock extends BlockRotation<PlantFertilizerTile> {

    public PlantFertilizerBlock() {
        super("plant_fertilizer", Properties.from(Blocks.IRON_BLOCK), PlantFertilizerTile.class);
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
