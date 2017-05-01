package com.buuz135.industrial.proxy;

import com.buuz135.industrial.fluid.BlockFluidXP;
import com.buuz135.industrial.tile.block.*;

public class BlockRegistry {

    public static PetrifiedFuelGeneratorBlock petrifiedFuelGeneratorBlock;
    public static EnchantmentRefinerBlock enchantmentRefinerBlock;
    public static EnchantmentExtractorBlock enchantmentExtractorBlock;
    public static EnchantmentAplicatorBlock enchantmentAplicatorBlock;
    public static MobRelocatorBlock mobRelocatorBlock;
    public static PotionEnervatorBlock potionEnervatorBlock;
    public static AnimalIndependenceSelectorBlock animalIndependenceSelectorBlock;
    public static AnimalStockIncreaserBlock animalStockIncreaserBlock;
    public static CropSowerBlock cropSowerBlock;
    public static CropEnrichMaterialInjectorBlock cropEnrichMaterialInjectorBlock;
    public static CropRecolectorBlock cropRecolectorBlock;
    public static BlackHoleUnitBlock blackHoleUnitBlock;
    public static WaterCondensatorBlock waterCondensatorBlock;

    public static BlockFluidXP XP;

    public static void registerBlocks() {
        (petrifiedFuelGeneratorBlock = new PetrifiedFuelGeneratorBlock()).register();
        (enchantmentRefinerBlock = new EnchantmentRefinerBlock()).register();
        (enchantmentExtractorBlock = new EnchantmentExtractorBlock()).register();
        (enchantmentAplicatorBlock = new EnchantmentAplicatorBlock()).register();
        (mobRelocatorBlock = new MobRelocatorBlock()).register();
        (potionEnervatorBlock = new PotionEnervatorBlock()).register();
        (animalIndependenceSelectorBlock = new AnimalIndependenceSelectorBlock()).register();
        (animalStockIncreaserBlock = new AnimalStockIncreaserBlock()).register();
        (cropSowerBlock = new CropSowerBlock()).register();
        (cropEnrichMaterialInjectorBlock = new CropEnrichMaterialInjectorBlock()).register();
        (cropRecolectorBlock = new CropRecolectorBlock()).register();
        (blackHoleUnitBlock = new BlackHoleUnitBlock()).register();
        (waterCondensatorBlock = new WaterCondensatorBlock()).register();

        (XP = new BlockFluidXP(FluidsRegistry.XP, "xp_block")).register();
    }
}
