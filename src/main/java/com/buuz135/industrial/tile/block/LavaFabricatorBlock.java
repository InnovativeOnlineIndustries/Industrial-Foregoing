package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.generator.LavaFabricatorTile;
import net.minecraft.block.material.Material;

public class LavaFabricatorBlock extends CustomOrientedBlock<LavaFabricatorTile> {
    public LavaFabricatorBlock() {
        super("lava_fabricator", LavaFabricatorTile.class, Material.ROCK, 200000, 200000);
    }
}
