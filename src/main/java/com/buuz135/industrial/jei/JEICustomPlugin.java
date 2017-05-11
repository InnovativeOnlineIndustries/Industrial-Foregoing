package com.buuz135.industrial.jei;


import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.block.SludgeRefinerBlock;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
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
        BlockRegistry.sludgeRefinerBlock.getItemStackWeightedItems().forEach(itemStackWeightedItem -> wrapperList.add(new SludgeRefinerRecipeWrapper(itemStackWeightedItem,maxWeight)));
        registry.addRecipes(wrapperList, sludgeRefinerRecipeCategory.getUid());
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.sludgeRefinerBlock), sludgeRefinerRecipeCategory.getUid());

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
