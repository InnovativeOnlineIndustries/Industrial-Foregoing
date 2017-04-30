package com.buuz135.industrial.proxy.client;

import com.buuz135.industrial.proxy.BlockRegistry;

public class BlockRenderRegistry {

    public static void registerRender() {
        BlockRegistry.petrifiedFuelGeneratorBlock.registerRenderer();
        BlockRegistry.enchantmentRefinerBlock.registerRenderer();
        BlockRegistry.enchantmentExtractorBlock.registerRenderer();
        BlockRegistry.enchantmentAplicatorBlock.registerRenderer();
        BlockRegistry.mobRelocatorBlock.registerRenderer();
    }
}
