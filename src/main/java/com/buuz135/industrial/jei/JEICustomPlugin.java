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

        registry.addDescription(new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock),"This generator will generate power depending the burntime of the solid fuel. The more burn time it has the more power/tick will create. (All fuels will burn the same amount of time)");
        registry.addDescription(new ItemStack(BlockRegistry.enchantmentRefinerBlock), "The enchantent refiner, when provider with power, it will sort enchanted items from unenchanted items.");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
