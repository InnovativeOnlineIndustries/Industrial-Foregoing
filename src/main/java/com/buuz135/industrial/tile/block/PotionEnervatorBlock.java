package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.magic.PotionEnervatorTile;
import net.minecraft.block.material.Material;

public class PotionEnervatorBlock extends CustomOrientedBlock<PotionEnervatorTile> {
    public PotionEnervatorBlock() {
        super("potion_enervator", PotionEnervatorTile.class, Material.ROCK, 5000, 40);
    }

}
