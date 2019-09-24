package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantSowerTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class PlantSowerBlock extends BlockRotation<PlantSowerTile> {

    public PlantSowerBlock() {
        super("plant_sower", Properties.from(Blocks.IRON_BLOCK), PlantSowerTile.class);
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
