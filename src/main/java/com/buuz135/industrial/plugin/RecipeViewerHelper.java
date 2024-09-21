package com.buuz135.industrial.plugin;

import com.buuz135.industrial.block.generator.tile.BioReactorTile;
import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.jei.StoneWorkWrapper;
import com.buuz135.industrial.plugin.jei.category.BioReactorRecipeCategory;
import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.hrznstudio.titanium.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeViewerHelper {

    public static List<BioReactorRecipeCategory.ReactorRecipeWrapper> generateBioreactorRecipes() {
        List<BioReactorRecipeCategory.ReactorRecipeWrapper> recipes = new ArrayList<>();
        for (TagKey<Item> itemTag : BioReactorTile.VALID) {
            recipes.add(new BioReactorRecipeCategory.ReactorRecipeWrapper(itemTag, new FluidStack(ModuleCore.BIOFUEL.getSourceFluid().get(), 80)));
        }
        return recipes;
    }

    public static ItemStack getStoneWorkOutputFrom(ItemStack stack, MaterialStoneWorkFactoryTile.StoneWorkAction mode) {
        return mode.getWork().apply(Minecraft.getInstance().level, stack.copyWithCount(9));
    }

    public static ItemStack getStoneWorkOutputFrom(ItemStack stack, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes) {
        for (MaterialStoneWorkFactoryTile.StoneWorkAction mode : modes) {
            stack = getStoneWorkOutputFrom(stack.copy(), mode);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    public static List<StoneWorkWrapper> findAllStoneWorkOutputs(ItemStack parent, List<MaterialStoneWorkFactoryTile.StoneWorkAction> usedModes) {
        List<StoneWorkWrapper> wrappers = new ArrayList<>();
        if (usedModes.size() >= 4) return wrappers;
        for (MaterialStoneWorkFactoryTile.StoneWorkAction mode : MaterialStoneWorkFactoryTile.ACTION_RECIPES) {
            if (mode.getAction().equals("none")) continue;
            List<MaterialStoneWorkFactoryTile.StoneWorkAction> usedModesInternal = new ArrayList<>(usedModes);
            usedModesInternal.add(mode);
            ItemStack output = getStoneWorkOutputFrom(parent, new ArrayList<>(usedModesInternal));
            if (!output.isEmpty()) {
                wrappers.add(new StoneWorkWrapper(parent, new ArrayList<>(usedModesInternal), output.copy()));
                wrappers.addAll(findAllStoneWorkOutputs(parent, new ArrayList<>(usedModesInternal)));
            }
        }
        return wrappers;
    }

    public static List<StoneWorkWrapper> getStoneWork() {
        List<StoneWorkWrapper> perfectStoneWorkWrappers = new ArrayList<>();
        for (StoneWorkGenerateRecipe generatorRecipe : RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<StoneWorkGenerateRecipe>) ModuleCore.STONEWORK_GENERATE_TYPE.get())) {
            List<StoneWorkWrapper> wrappers = findAllStoneWorkOutputs(generatorRecipe.output, new ArrayList<>());
            for (StoneWorkWrapper workWrapper : new ArrayList<>(wrappers)) {
                if (perfectStoneWorkWrappers.stream().noneMatch(stoneWorkWrapper -> ItemStack.isSameItem(workWrapper.output(), stoneWorkWrapper.output()))) {
                    boolean isSomoneShorter = false;
                    for (StoneWorkWrapper workWrapperCompare : new ArrayList<>(wrappers)) {
                        if (ItemStack.isSameItem(workWrapper.output(), workWrapperCompare.output())) {
                            List<MaterialStoneWorkFactoryTile.StoneWorkAction> workWrapperCompareModes = new ArrayList<>(workWrapperCompare.modes());
                            workWrapperCompareModes.removeIf(mode -> mode.getAction().equalsIgnoreCase("none"));
                            List<MaterialStoneWorkFactoryTile.StoneWorkAction> workWrapperModes = new ArrayList<>(workWrapper.modes());
                            workWrapperModes.removeIf(mode -> mode.getAction().equalsIgnoreCase("none"));
                            if (workWrapperModes.size() > workWrapperCompareModes.size()) {
                                isSomoneShorter = true;
                                break;
                            }
                        }
                    }
                    if (!isSomoneShorter) perfectStoneWorkWrappers.add(workWrapper);
                }
            }
        }
        return perfectStoneWorkWrappers;
    }
}
