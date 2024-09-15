/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.TagUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.List;

public class LaserDrillOreRecipe implements Recipe<CraftingInput> {

    public static final MapCodec<LaserDrillOreRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            Ingredient.CODEC.fieldOf("output").forGetter(o -> o.output),
            Ingredient.CODEC.fieldOf("catalyst").forGetter(o -> o.catalyst),
            LaserDrillRarity.CODEC.listOf().fieldOf("rarity").forGetter(o -> o.rarity)
    ).apply(in, LaserDrillOreRecipe::new));
    public List<LaserDrillRarity> rarity;

    public LaserDrillOreRecipe(Ingredient output, Ingredient catalyst, LaserDrillRarity... rarity) {
        this(output, catalyst, List.of(rarity));
    }

    public LaserDrillOreRecipe(Ingredient output, Ingredient catalyst, List<LaserDrillRarity> rarity) {
        this.output = output;
        this.catalyst = catalyst;
        this.rarity = rarity;
    }

    public LaserDrillOreRecipe() {
    }

    public LaserDrillOreRecipe(Ingredient output, int color, LaserDrillRarity... rarity) {
        this(output, Ingredient.of(ModuleCore.LASER_LENS[color].get()), rarity);
    }

    public static void init(RecipeOutput output) {
        createWithDefault(output, Blocks.COAL_ORE, 15, 5, 132, 10, 4);
        createWithDefault(output, "raw_materials/iron", 12, 5, 68, 20, 3);
        createWithDefault(output, Blocks.REDSTONE_ORE, 14, 5, 16, 28, 4);
        createTagRecipe(output, "raw_materials/gold", 4,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(BiomeTags.HAS_MINESHAFT_MESA), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of()),
                        32, 80, 16),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        5, 32, 6),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, 1));
        createWithDefault(output, Blocks.LAPIS_ORE, 11, 13, 34, 14, 2);
        createItemRecipe(output, Blocks.EMERALD_ORE, 5,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(BiomeTags.IS_MOUNTAIN), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of()),
                        5, 29, 8),
                new LaserDrillRarity(new LaserDrillRarity.BiomeRarity(List.of(BiomeTags.IS_MOUNTAIN), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of()),
                        0, 255, 1));
        createWithDefault(output, Blocks.DIAMOND_ORE, 3, 5, 16, 4, 1);
        createItemRecipe(output, Blocks.NETHER_QUARTZ_ORE, 0,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), List.of()),
                        7, 117, 12),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, 1));
        createItemRecipe(output, Blocks.GLOWSTONE, 4,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), List.of()), 7, 117, 8),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, 1));
        createTagRecipe(output, "raw_materials/uranium", 5,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), List.of()),
                        5, 29, 5),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, 1));
        createWithDefault(output, "ores/sulfur", 4, 5, 10, 6, 1);
        createWithDefault(output, "raw_materials/galena", 10, 15, 30, 6, 1);
        createTagRecipe(output, "raw_materials/iridium", 0,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.END), List.of()),
                        5, 68, 8),
                new LaserDrillRarity(new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, 1));
        createWithDefault(output, "ores/ruby", 14, 30, 70, 6, 1);
        createWithDefault(output, "ores/sapphire", 11, 30, 70, 6, 1);
        createWithDefault(output, "ores/peridot", 13, 30, 70, 6, 1);
        createWithDefault(output, "ores/sodalite", 11, 30, 70, 6, 1);
        createWithDefault(output, "ores/yellorite", 4, 16, 68, 3, 1);
        createWithDefault(output, "ores/cinnabar", 14, 30, 70, 2, 1);
        createWithDefault(output, "raw_materials/bauxite", 12, 50, 100, 6, 1);
        createWithDefault(output, "raw_materials/pyrite", 12, 30, 70, 3, 1);
        createWithDefault(output, "raw_materials/cinnabar", 14, 30, 70, 2, 1);
        createEnd(output, "raw_materials/tungsten", 15, 20, 70, 4);
        createEnd(output, "raw_materials/sheldonite", 0, 30, 70, 6);
        createWithDefault(output, "raw_materials/platinum", 3, 5, 16, 3, 1);
        createWithDefault(output, "raw_materials/tetrahedrite", 14, 60, 90, 4, 1);
        createWithDefault(output, "raw_materials/tin", 8, 64, 96, 8, 2);
        createWithDefault(output, "raw_materials/lead", 10, 10, 40, 6, 1);
        createWithDefault(output, "raw_materials/silver", 7, 10, 40, 5, 1);
        createWithDefault(output, "raw_materials/copper", 1, 35, 65, 10, 2);
        createWithDefault(output, "raw_materials/aluminum", 12, 68, 84, 5, 1);
        createWithDefault(output, "raw_materials/nickel", 12, 5, 68, 4, 1);
        createEnd(output, "ores/draconium", 10, 60, 95, 10);
        createWithDefault(output, "raw_materials/yellorium", 4, 16, 68, 3, 1);
        createNether(output, "raw_materials/cobalt", 11, 34, 96, 8);
        createNether(output, "raw_materials/ardite", 4, 89, 116, 8);
        createItemRecipe(output, Blocks.ANCIENT_DEBRIS, 12,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), List.of())
                        , 1, 20, 1));
        createWithDefault(output, "ores/niter", 4, 5, 20, 2, 1);
        createWithDefault(output, "ores/arcane", 2, 45, 60, 3, 1);
        createWithDefault(output, "ores/bitumen", 15, 50, 60, 2, 1);
        createWithDefault(output, "ores/fluorite", 8, 15, 30, 6, 1);
        createWithDefault(output, "raw_materials/osmium", 8, 5, 36, 8, 1);
        createWithDefault(output, "raw_materials/yellorite", 4, 15, 50, 3, 1);
    }

    public static void createWithDefault(RecipeOutput recipeOutput, ItemLike output, int color, int min, int max, int weight, int defaultWeight) {
        createItemRecipe(recipeOutput, output, color, createDefaultRarities(min, max, weight, defaultWeight));
    }

    public static void createWithDefault(RecipeOutput recipeOutput, String name, int color, int min, int max, int weight, int defaultWeight) {
        createTagRecipe(recipeOutput, name, color, createDefaultRarities(min, max, weight, defaultWeight));
    }

    private static LaserDrillRarity[] createDefaultRarities(int min, int max, int weight, int defaultWeight) {
        return new LaserDrillRarity[]{
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        min, max, weight),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(), List.of(BuiltinDimensionTypes.END)),
                        0, 255, defaultWeight)};
    }

    public Ingredient output;

    public static void createEnd(RecipeOutput recipeOutput, String name, int color, int min, int max, int weight) {
        createTagRecipe(recipeOutput, name, color,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.END), List.of()),
                        min, max, weight));
    }
    public int pointer = 0;
    public Ingredient catalyst;

    public static void createNether(RecipeOutput recipeOutput, String name, int color, int min, int max, int weight) {
        createTagRecipe(recipeOutput, name, color,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(List.of(), List.of()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), List.of()), min, max, weight));
    }

    public static void createItemRecipe(RecipeOutput recipeOutput, ItemLike itemLike, int color, LaserDrillRarity... rarity) {
        var output = Ingredient.of(itemLike);
        var recipe = new LaserDrillOreRecipe(output, color, rarity);
        var rl = generateRL(BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath());
        var advancementHolder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(rl))
                .rewards(AdvancementRewards.Builder.recipe(rl))
                .requirements(AdvancementRequirements.Strategy.OR).build(rl);
        recipeOutput.accept(rl, recipe, advancementHolder);
    }

    public static void createTagRecipe(RecipeOutput recipeOutput, String tagString, int color, LaserDrillRarity... rarity) {
        var tag = TagUtil.getItemTag(ResourceLocation.fromNamespaceAndPath("c", tagString));
        var output = Ingredient.of(tag);
        var recipe = new LaserDrillOreRecipe(output, color, rarity);
        var rl = generateRL(tagString);
        var advancementHolder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(rl))
                .rewards(AdvancementRewards.Builder.recipe(rl))
                .requirements(AdvancementRequirements.Strategy.OR).build(rl);
        recipeOutput.accept(rl, recipe, advancementHolder, new NotCondition(new TagEmptyCondition(tag)));
    }

    public static ResourceLocation generateRL(String key) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "laser_drill_ore/" + key);
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModuleCore.LASER_DRILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.LASER_DRILL_TYPE.get();
    }

    /*@Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (isTag != null) {
            return Pair.of(new NotCondition(new TagEmptyCondition(isTag)), NotCondition.Serializer.INSTANCE);
        }
        return null;
    }*/

}
