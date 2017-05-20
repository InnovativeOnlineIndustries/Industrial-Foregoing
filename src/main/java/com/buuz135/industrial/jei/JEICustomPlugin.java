package com.buuz135.industrial.jei;


import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeCategory;
import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeWrapper;
import com.buuz135.industrial.jei.laser.LaserRecipeCategory;
import com.buuz135.industrial.jei.laser.LaserRecipeWrapper;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeCategory;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeWrapper;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;


@JEIPlugin
public class JEICustomPlugin implements IModPlugin {
    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {


    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void register(IModRegistry registry) {

        SludgeRefinerRecipeCategory sludgeRefinerRecipeCategory = new SludgeRefinerRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(sludgeRefinerRecipeCategory);
        int maxWeight = WeightedRandom.getTotalWeight(BlockRegistry.sludgeRefinerBlock.getItemStackWeightedItems());
        List<SludgeRefinerRecipeWrapper> wrapperList = new ArrayList<>();
        BlockRegistry.sludgeRefinerBlock.getItemStackWeightedItems().forEach(itemStackWeightedItem -> wrapperList.add(new SludgeRefinerRecipeWrapper(itemStackWeightedItem, maxWeight)));
        registry.addRecipes(wrapperList, sludgeRefinerRecipeCategory.getUid());
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.sludgeRefinerBlock), sludgeRefinerRecipeCategory.getUid());

        BioReactorRecipeCategory bioReactorRecipeCategory = new BioReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(bioReactorRecipeCategory);
        List<BioReactorRecipeWrapper> bioreactor = new ArrayList<>();
        BlockRegistry.bioReactorBlock.getItemsAccepted().forEach(stack -> bioreactor.add(new BioReactorRecipeWrapper(stack)));
        registry.addRecipes(bioreactor, bioReactorRecipeCategory.getUid());
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.bioReactorBlock), bioReactorRecipeCategory.getUid());

        LaserRecipeCategory laserRecipeCategory = new LaserRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(laserRecipeCategory);
        List<ItemStackWeightedItem> item = new ArrayList<>();
        BlockRegistry.laserBaseBlock.getColoreOres().keySet().forEach(integer -> item.addAll(BlockRegistry.laserBaseBlock.getColoreOres().get(integer)));
        final int laserMaxWeight = WeightedRandom.getTotalWeight(item);
        List<LaserRecipeWrapper> laserRecipeWrappers = new ArrayList<>();
        BlockRegistry.laserBaseBlock.getColoreOres().keySet().forEach(integer -> BlockRegistry.laserBaseBlock.getColoreOres().get(integer).forEach(temp -> laserRecipeWrappers.add(new LaserRecipeWrapper(temp, laserMaxWeight, integer))));
        registry.addRecipes(laserRecipeWrappers, laserRecipeCategory.getUid());
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.laserDrillBlock), laserRecipeCategory.getUid());
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.laserBaseBlock),laserRecipeCategory.getUid());


        //Descriptions
        registry.addDescription(new ItemStack(ItemRegistry.meatFeederItem),"The meat feeder will keep fed if it has liquid meat. (Don't ask where the meat comes, you won't like it)");
        registry.addDescription(new ItemStack(ItemRegistry.mobImprisonmentToolItem), "This tool can capture mobs to be used in the Mob duplicator.");
        registry.addDescription(new ItemStack(ItemRegistry.tinyDryRubber), "Produced in the latex processing unit.");
        registry.addDescription(new ItemStack(ItemRegistry.fertilizer), "It can be used in the Plant Enrich Material Injector to make plants frow faster. Produced in the Sewer Compost Solidifier");
        registry.addDescription(new ItemStack(ItemRegistry.laserLensItem, 1, OreDictionary.WILDCARD_VALUE), "When used in the laser it will increase the chance of the same colored ores.");
        registry.addDescription(new ItemStack(ItemRegistry.adultFilterAddomItem), "It will change the filter to adults to the Animal Independence Selector.");
        registry.addDescription(new ItemStack(ItemRegistry.rangeAddonItem, 1, OreDictionary.WILDCARD_VALUE), "The range addon will increase the range of the working area of some machines.");

        registry.addDescription(new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock),"This generator will generate power depending the burntime of the solid fuel. The more burn time it has the more power/tick will create. (All fuels will burn the same amount of time)");
        registry.addDescription(new ItemStack(BlockRegistry.enchantmentRefinerBlock), "The enchantent refiner, when provider with power, it will sort enchanted items from unenchanted items.");
        registry.addDescription(new ItemStack(BlockRegistry.enchantmentExtractorBlock), "When provided with books and enchanted items it will remove the enchantment of the item and apply it to the book.");
        registry.addDescription(new ItemStack(BlockRegistry.enchantmentAplicatorBlock), "When provided with an Essence, enchanted books and items it will apply the enchantment tothe item.");
        registry.addDescription(new ItemStack(BlockRegistry.mobRelocatorBlock), "The Mob Relocator will kill any mob in front of it and produce some essence based on the health it has.");
        registry.addDescription(new ItemStack(BlockRegistry.potionEnervatorBlock), "An advanced version of the Brewing Stand.");
        registry.addDescription(new ItemStack(BlockRegistry.animalIndependenceSelectorBlock), "The Animal Independence Selector will move any baby animal (by default) from the front area, to the back area.");
        registry.addDescription(new ItemStack(BlockRegistry.animalStockIncreaserBlock), "The Animal Stock Increaser will feed animals with their prefered food.");
        registry.addDescription(new ItemStack(BlockRegistry.cropSowerBlock), "Plants crops and saplings.");
        registry.addDescription(new ItemStack(BlockRegistry.cropEnrichMaterialInjectorBlock), "Consumes bone meal or fetilizer to make plants grow faster.");
        registry.addDescription(new ItemStack(BlockRegistry.cropRecolectorBlock), "Recolects full grown crops and chops down trees.");
        registry.addDescription(new ItemStack(BlockRegistry.blackHoleUnitBlock), "Holds almost an infinite amount of one item. It will keep the contents when broken.");
        registry.addDescription(new ItemStack(BlockRegistry.waterCondensatorBlock), "Creates water when 2 or more water sources are touching one of the sides of the machine.");
        registry.addDescription(new ItemStack(BlockRegistry.waterResourcesCollectorBlock), "When placed in top of a 3x3 of water it will collect resources from it. (Some people say that there is a tiny villager inside of it)");
        registry.addDescription(new ItemStack(BlockRegistry.animalResourceHarvesterBlock), "Collects milk and wool from animals.");
        registry.addDescription(new ItemStack(BlockRegistry.mobRelocatorBlock), "Grinds down mobs to produce meat.");
        registry.addDescription(new ItemStack(BlockRegistry.mobDuplicatorBlock), "Clones mobs when provided with with Essence and a filled Mob Imprisonment tool");
        registry.addDescription(new ItemStack(BlockRegistry.blockDestroyerBlock), "Breaks down blocks in front of it.");
        registry.addDescription(new ItemStack(BlockRegistry.blockPlacerBlock), "Places down blocks in front of it from the inventory.");
        registry.addDescription(new ItemStack(BlockRegistry.treeFluidExtractorBlock), "Collects latex from blocks of wood in front of it.");
        registry.addDescription(new ItemStack(BlockRegistry.latexProcessingUnitBlock), "Creates tiny rubber from latex and water.");
        registry.addDescription(new ItemStack(BlockRegistry.sewageCompostSolidiferBlock), "Produces fertilizer from Sewage.");
        registry.addDescription(new ItemStack(BlockRegistry.animalByproductRecolectorBlock), "Collects sewage from animals.");
        registry.addDescription(new ItemStack(BlockRegistry.sludgeRefinerBlock), "Refines Sludge into better items.");
        registry.addDescription(new ItemStack(BlockRegistry.mobDetectorBlock), "Emits a redstone signal to the back depending of the amount of mobs it has in front.");
        registry.addDescription(new ItemStack(BlockRegistry.lavaFabricatorBlock), "Creates lava using a big amount of energy.");
        registry.addDescription(new ItemStack(BlockRegistry.bioReactorBlock), "Creates biofuel using different types of plants.");
        registry.addDescription(new ItemStack(BlockRegistry.biofuelGeneratorBlock), "Creates power using biofuel.");
        registry.addDescription(new ItemStack(BlockRegistry.laserBaseBlock), "Creates ores when charged with Laser Drills, can be focused with Laser Lenses");
        registry.addDescription(new ItemStack(BlockRegistry.laserDrillBlock), "Charges the Laser Base when it is a block away from it.");
        registry.addDescription(new ItemStack(BlockRegistry.oreProcessorBlock), "Breaks down ores into components.");
        registry.addDescription(new ItemStack(BlockRegistry.sliderBlock), "Pushes entites around.");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
