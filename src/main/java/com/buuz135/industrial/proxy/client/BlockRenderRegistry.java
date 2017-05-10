package com.buuz135.industrial.proxy.client;

import static com.buuz135.industrial.proxy.BlockRegistry.*;

public class BlockRenderRegistry {

    public static void registerRender() {
        petrifiedFuelGeneratorBlock.registerRenderer();
        enchantmentRefinerBlock.registerRenderer();
        enchantmentExtractorBlock.registerRenderer();
        enchantmentAplicatorBlock.registerRenderer();
        mobRelocatorBlock.registerRenderer();
        potionEnervatorBlock.registerRenderer();
        animalIndependenceSelectorBlock.registerRenderer();
        animalStockIncreaserBlock.registerRenderer();
        cropSowerBlock.registerRenderer();
        cropEnrichMaterialInjectorBlock.registerRenderer();
        cropRecolectorBlock.registerRenderer();
        blackHoleUnitBlock.registerRenderer();
        waterCondensatorBlock.registerRenderer();
        waterResourcesCollectorBlock.registerRenderer();
        animalResourceHarvesterBlock.registerRenderer();
        mobSlaughterFactoryBlock.registerRenderer();
        mobDuplicatorBlock.registerRenderer();
        blockDestroyerBlock.registerRenderer();
        blockPlacerBlock.registerRenderer();
        treeFluidExtractorBlock.registerRenderer();
        latexProcessingUnitBlock.registerRenderer();
        sewageCompostSolidiferBlock.registerRenderer();
        animalByproductRecolectorBlock.registerRenderer();
        sludgeRefinerBlock.registerRenderer();

        sliderBlock.registerRenderer();
    }
}
