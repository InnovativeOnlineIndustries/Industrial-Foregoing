package com.buuz135.industrial.config;

import net.minecraftforge.common.config.Configuration;

import static com.buuz135.industrial.proxy.BlockRegistry.*;

public class CustomConfiguration {

    public static Configuration config;

    public static void sync() {
        try {
            config.load();
            petrifiedFuelGeneratorBlock.getMachineConfig();
            enchantmentRefinerBlock.getMachineConfig();
            enchantmentExtractorBlock.getMachineConfig();
            enchantmentAplicatorBlock.getMachineConfig();
            mobRelocatorBlock.getMachineConfig();
            potionEnervatorBlock.getMachineConfig();
            animalIndependenceSelectorBlock.getMachineConfig();
            animalStockIncreaserBlock.getMachineConfig();
            cropSowerBlock.getMachineConfig();
            cropEnrichMaterialInjectorBlock.getMachineConfig();
            cropRecolectorBlock.getMachineConfig();
            blackHoleUnitBlock.getMachineConfig();
            waterCondensatorBlock.getMachineConfig();
            waterResourcesCollectorBlock.getMachineConfig();
            animalResourceHarvesterBlock.getMachineConfig();
            mobSlaughterFactoryBlock.getMachineConfig();
            mobDuplicatorBlock.getMachineConfig();
            blockDestroyerBlock.getMachineConfig();
            blockPlacerBlock.getMachineConfig();
            treeFluidExtractorBlock.getMachineConfig();
            latexProcessingUnitBlock.getMachineConfig();
            sewageCompostSolidiferBlock.getMachineConfig();
            animalByproductRecolectorBlock.getMachineConfig();
            sludgeRefinerBlock.getMachineConfig();
            mobDetectorBlock.getMachineConfig();
            lavaFabricatorBlock.getMachineConfig();
            bioReactorBlock.getMachineConfig();
            biofuelGeneratorBlock.getMachineConfig();
            laserBaseBlock.getMachineConfig();
            laserDrillBlock.getMachineConfig();
            oreProcessorBlock.getMachineConfig();
            blackHoleControllerBlock.getMachineConfig();
            dyeMixerBlock.getMachineConfig();
            enchantmentInvokerBlock.getMachineConfig();
            sporesRecreatorBlock.getMachineConfig();
            animalGrowthIncreaserBlock.getMachineConfig();
            materialStoneWorkFactoryBlock.getMachineConfig();
            blackHoleTankBlock.getMachineConfig();
            //sliderBlock.getMachineConfig();
        } catch (Exception e) {

        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
