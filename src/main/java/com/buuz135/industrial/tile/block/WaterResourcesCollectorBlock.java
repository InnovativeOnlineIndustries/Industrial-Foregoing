package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.animal.WaterResourcesCollectorTile;
import net.minecraft.block.material.Material;

public class WaterResourcesCollectorBlock extends CustomOrientedBlock<WaterResourcesCollectorTile> {

    public WaterResourcesCollectorBlock() {
        super("water_resources_collector", WaterResourcesCollectorTile.class, Material.ROCK);
    }
}
