package com.buuz135.industrial.jei;


import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeCategory;
import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeWrapper;
import com.buuz135.industrial.jei.laser.LaserRecipeCategory;
import com.buuz135.industrial.jei.laser.LaserRecipeWrapper;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeCategory;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeWrapper;
import com.buuz135.industrial.proxy.BlockRegistry;
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

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
