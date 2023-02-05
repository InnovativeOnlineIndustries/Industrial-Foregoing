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
import com.hrznstudio.titanium.recipe.serializer.GenericSerializer;
import com.hrznstudio.titanium.recipe.serializer.SerializableRecipe;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserDrillOreRecipe extends SerializableRecipe {

    public static List<LaserDrillOreRecipe> RECIPES = new ArrayList<>();

    public static void init() {
        createWithDefault("coal", Blocks.COAL_ORE, 15, 5, 132, 10, 4);
        createWithDefault("raw_materials/iron", 12, 5, 68, 20, 3);
        createWithDefault("redstone", Blocks.REDSTONE_ORE, 14, 5, 16, 28, 4);
        new LaserDrillOreRecipe("raw_materials/gold", Ingredient.of(TagUtil.getItemTag(new ResourceLocation("forge", "raw_materials/gold"))), 4, null,
                new LaserDrillRarity(new ResourceKey[]{Biomes.BADLANDS, Biomes.ERODED_BADLANDS}, new ResourceKey[0], 32, 80, 16),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 5, 32, 6),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("lapis", Blocks.LAPIS_ORE, 11, 13, 34, 14, 2);
        new LaserDrillOreRecipe("emerald", Ingredient.of(Blocks.EMERALD_ORE), 5, null,
                new LaserDrillRarity(new ResourceKey[]{Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS}, new ResourceKey[0], 5, 29, 8),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("diamond", Blocks.DIAMOND_ORE, 3, 5, 16, 4, 1);
        new LaserDrillOreRecipe("quartz", Ingredient.of(Blocks.NETHER_QUARTZ_ORE), 0, null,
                new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], 7, 117, 12),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        new LaserDrillOreRecipe("glowstone", Ingredient.of(Blocks.GLOWSTONE), 4, null,
                new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], 7, 117, 8),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        new LaserDrillOreRecipe("raw_materials/uranium", Ingredient.of(TagUtil.getItemTag(new ResourceLocation("forge", "raw_materials/uranium"))), 5, new ResourceLocation("forge", "raw_materials/uranium"),
                new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], 5, 29, 5),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("ores/sulfur", 4, 5, 10, 6, 1);
        createWithDefault("raw_materials/galena", 10, 15, 30, 6, 1);
        new LaserDrillOreRecipe("raw_materials/iridium", Ingredient.of(TagUtil.getItemTag(new ResourceLocation("forge", "raw_materials/iridium"))), 0, new ResourceLocation("forge", "raw_materials/iridium"),
                new LaserDrillRarity(LaserDrillRarity.END, new ResourceKey[0], 5, 68, 8),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, 1));
        createWithDefault("ores/ruby", 14, 30, 70, 6, 1);
        createWithDefault("ores/sapphire", 11, 30, 70, 6, 1);
        createWithDefault("ores/peridot", 13, 30, 70, 6, 1);
        createWithDefault("ores/sodalite", 11, 30, 70, 6, 1);
        createWithDefault("ores/yellorite", 4, 16, 68, 3, 1);
        createWithDefault("ores/cinnabar", 14, 30, 70, 2, 1);
        createWithDefault("raw_materials/bauxite", 12, 50, 100, 6, 1);
        createWithDefault("raw_materials/pyrite", 12, 30, 70, 3, 1);
        createWithDefault("raw_materials/cinnabar", 14, 30, 70, 2, 1);
        createEnd("raw_materials/tungsten", 15, 20, 70, 4);
        createEnd("raw_materials/sheldonite", 0, 30, 70, 6);
        createWithDefault("raw_materials/platinum", 3, 5, 16, 3, 1);
        createWithDefault("raw_materials/tetrahedrite", 14, 60, 90, 4, 1);
        createWithDefault("raw_materials/tin", 8, 64, 96, 8, 2);
        createWithDefault("raw_materials/lead", 10, 10, 40, 6, 1);
        createWithDefault("raw_materials/silver", 7, 10, 40, 5, 1);
        createWithDefault("raw_materials/copper", 1, 35, 65, 10, 2);
        createWithDefault("raw_materials/aluminum", 12, 68, 84, 5, 1);
        createWithDefault("raw_materials/nickel", 12, 5, 68, 4, 1);
        createEnd("ores/draconium", 10, 60, 95, 10);
        createWithDefault("raw_materials/yellorium", 4, 16, 68, 3, 1);
        createNether("raw_materials/cobalt", 11, 34, 96, 8);
        createNether("raw_materials/ardite", 4, 89, 116, 8);
        new LaserDrillOreRecipe("ancient_debris", Ingredient.of(Blocks.ANCIENT_DEBRIS), 12, null, new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], 1, 20, 1));
        createWithDefault("ores/niter", 4, 5, 20, 2, 1);
        createWithDefault("ores/arcane", 2, 45, 60, 3, 1);
        createWithDefault("ores/bitumen", 15, 50, 60, 2, 1);
        createWithDefault("ores/fluorite", 8, 15, 30, 6, 1);
        createWithDefault("raw_materials/osmium", 8, 5, 36, 8, 1);
        createWithDefault("raw_materials/yellorite", 4, 15, 50, 3, 1);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, ItemLike output, int color, int min, int max, int weight, int defaultWeight) {
        return createWithDefault(name, Ingredient.of(output), color, min, max, weight, defaultWeight, null);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, int color, int min, int max, int weight, int defaultWeight) {
        ResourceLocation rl = new ResourceLocation("forge", name);
        return createWithDefault(name, Ingredient.of(TagUtil.getItemTag(rl)), color, min, max, weight, defaultWeight, rl);
    }

    public static LaserDrillOreRecipe createWithDefault(String name, Ingredient output, int color, int min, int max, int weight, int defaultWeight, ResourceLocation isTag) {
        return new LaserDrillOreRecipe(name, output, color, isTag,
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, min, max, weight),
                new LaserDrillRarity(new ResourceKey[0], LaserDrillRarity.END, 0, 255, defaultWeight));
    }

    public static LaserDrillOreRecipe createEnd(String name, int color, int min, int max, int weight) {
        ResourceLocation rl = new ResourceLocation("forge", "ores/" + name);
        return createEnd(name, Ingredient.of(TagUtil.getItemTag(rl)), color, min, max, weight, rl);
    }

    public static LaserDrillOreRecipe createEnd(String name, Ingredient output, int color, int min, int max, int weight, ResourceLocation isTag) {
        return new LaserDrillOreRecipe(name, output, color, isTag,
                new LaserDrillRarity(LaserDrillRarity.END, new ResourceKey[0], min, max, weight));
    }

    public static LaserDrillOreRecipe createNether(String name, int color, int min, int max, int weight) {
        ResourceLocation rl = new ResourceLocation("forge", "ores/" + name);
        return createNether(name, Ingredient.of(TagUtil.getItemTag(rl)), color, min, max, weight, rl);
    }

    public static LaserDrillOreRecipe createNether(String name, Ingredient output, int color, int min, int max, int weight, ResourceLocation isTag) {
        return new LaserDrillOreRecipe(name, output, color, isTag,
                new LaserDrillRarity(LaserDrillRarity.NETHER, new ResourceKey[0], min, max, weight));
    }

    public Ingredient output;
    public LaserDrillRarity[] rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    private ResourceLocation isTag;

    public LaserDrillOreRecipe(String name, Ingredient output, Ingredient catalyst, ResourceLocation isTag, LaserDrillRarity... rarity) {
        super(new ResourceLocation(Reference.MOD_ID, name));
        this.output = output;
        this.catalyst = catalyst;
        this.rarity = rarity;
        this.isTag = isTag;
        RECIPES.add(this);
    }

    public LaserDrillOreRecipe(String name, Ingredient output, int color, ResourceLocation isTag, LaserDrillRarity... rarity) {
        this(name, output, Ingredient.of(ModuleCore.LASER_LENS[color].get()), isTag, rarity);
    }

    public LaserDrillOreRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return (GenericSerializer<? extends SerializableRecipe>) ModuleCore.LASER_DRILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.LASER_DRILL_TYPE.get();
    }

    @Override
    public Pair<ICondition, IConditionSerializer> getOutputCondition() {
        if (isTag != null) {
            return Pair.of(new NotCondition(new TagEmptyCondition(isTag)), NotCondition.Serializer.INSTANCE);
        }
        return null;
    }

    @Nullable
    public LaserDrillRarity getValidRarity(ResourceLocation biome, int height) {
        for (LaserDrillRarity laserDrillRarity : rarity) {
            if (laserDrillRarity.depth_max >= height && laserDrillRarity.depth_min <= height) {
                if (laserDrillRarity.whitelist.length == 0 ? Arrays.stream(laserDrillRarity.blacklist).noneMatch(registryKey -> registryKey.location().equals(biome)) : Arrays.stream(laserDrillRarity.whitelist).anyMatch(registryKey -> registryKey.location().equals(biome)))
                    return laserDrillRarity;
            }
        }
        return null;
    }
}
