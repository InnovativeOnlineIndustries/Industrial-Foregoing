package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.EnchantmentExtractorTile;
import net.minecraft.block.material.Material;

public class EnchantmentExtractorBlock extends CustomOrientedBlock<EnchantmentExtractorTile> {

    public EnchantmentExtractorBlock() {
        super("enchantment_extractor", EnchantmentExtractorTile.class, Material.ROCK);
    }
}
