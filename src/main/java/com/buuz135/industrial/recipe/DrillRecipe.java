package com.buuz135.industrial.recipe;

import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.data.EntityData;
import com.buuz135.industrial.utils.Reference;

import java.util.*;

public abstract class DrillRecipe<I> implements Recipe<CraftingInput> {

    public static ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
    
    public I output;
    public List<LaserDrillRarity> rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    public Optional<EntityData> entityData;

    public DrillRecipe(I output, Ingredient catalyst, Optional<EntityData> data, List<LaserDrillRarity> rarity) {
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        this.entityData = data;
    }
    public DrillRecipe(I output, Ingredient catalyst, Optional<EntityData> data, LaserDrillRarity... rarity) {
        this(output, catalyst, data, Arrays.asList(rarity));
    }
    public DrillRecipe(I output, int color, Optional<EntityData> data, LaserDrillRarity... rarity) {
        this(output, Ingredient.of(ModuleCore.LASER_LENS[color].get()), data, rarity);
    }

    public DrillRecipe(I output, int color, LaserDrillRarity... rarity) {
        this(output, Ingredient.of(ModuleCore.LASER_LENS[color].get()), Optional.empty(), rarity);
    }

    public static void createRecipe(RecipeOutput recipeOutput, String name, String modIdCondition, DrillRecipe<?> recipe) {
        var rl = generateRL(name);
        var advancementHolder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(rl))
                .rewards(AdvancementRewards.Builder.recipe(rl))
                .requirements(AdvancementRequirements.Strategy.OR).build(rl);
        recipeOutput.accept(rl, recipe, advancementHolder, new ModLoadedCondition(modIdCondition));
    }

    public static ResourceLocation generateRL(String key) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "laser_drill_fluid/" + key);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public abstract RecipeSerializer<?> getSerializer();
    
    public abstract RecipeType<?> getType();
}
