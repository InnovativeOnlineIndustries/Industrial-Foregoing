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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.desht.pneumaticcraft.common.registry.ModFluids;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaserDrillFluidRecipe implements Recipe<CraftingInput> {

    public static final MapCodec<LaserDrillFluidRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            FluidStack.CODEC.fieldOf("output").forGetter(o -> o.output),
            Ingredient.CODEC.fieldOf("catalyst").forGetter(o -> o.catalyst),
            ResourceLocation.CODEC.fieldOf("entity").forGetter(o -> o.entity),
            LaserDrillRarity.CODEC.listOf().fieldOf("rarity").forGetter(o -> o.rarity)
    ).apply(in, LaserDrillFluidRecipe::new));

    public static ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
    public FluidStack output;
    public List<LaserDrillRarity> rarity;
    public int pointer = 0;
    public Ingredient catalyst;
    public ResourceLocation entity;

    public LaserDrillFluidRecipe(FluidStack output, Ingredient catalyst, ResourceLocation entity, List<LaserDrillRarity> rarity) {
        this.output = output;
        this.rarity = rarity;
        this.catalyst = catalyst;
        this.entity = entity;
    }
    public LaserDrillFluidRecipe(FluidStack output, Ingredient catalyst, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(output, catalyst, entity, Arrays.asList(rarity));
    }
    public LaserDrillFluidRecipe(FluidStack output, int color, ResourceLocation entity, LaserDrillRarity... rarity) {
        this(output, Ingredient.of(ModuleCore.LASER_LENS[color].get()), entity, rarity);
    }

    public LaserDrillFluidRecipe() {
    }

    public static void init(RecipeOutput output) {
        createRecipe(output, "lava", "minecraft", new LaserDrillFluidRecipe(new FluidStack(Fluids.LAVA, 100), 1, EMPTY,
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(LaserDrillRarity.BiomeRarity.NETHER, new ArrayList<>()),
                        new LaserDrillRarity.DimensionRarity(List.of(BuiltinDimensionTypes.NETHER), new ArrayList<>()),
                        5, 20, 8)));
        createRecipe(output, "ether", "minecraft", new LaserDrillFluidRecipe(new FluidStack(ModuleCore.ETHER.getSourceFluid().get(), 10), 10, ResourceLocation.fromNamespaceAndPath("minecraft", "wither"),
                new LaserDrillRarity(
                        new LaserDrillRarity.BiomeRarity(new ArrayList<>(), new ArrayList<>()),
                        new LaserDrillRarity.DimensionRarity(new ArrayList<>(), new ArrayList<>()),
                        -64, 256, 8)));
        if (ModList.get().isLoaded("pneumaticcraft")) {
            createRecipe(output, "oil", "pneumaticcraft",
                    new LaserDrillFluidRecipe(new FluidStack(ModFluids.OIL.get(), 50), 15, EMPTY,
                            new LaserDrillRarity(
                                    new LaserDrillRarity.BiomeRarity(LaserDrillRarity.BiomeRarity.OIL, new ArrayList<>()),
                                    new LaserDrillRarity.DimensionRarity(new ArrayList<>(), new ArrayList<>()),
                                    20, 60, 8)));
        }
    }

    public static void createRecipe(RecipeOutput recipeOutput, String name, String modIdCondition, LaserDrillFluidRecipe recipe) {
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModuleCore.LASER_DRILL_FLUID_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.LASER_DRILL_FLUID_TYPE.get();
    }


    public static CompoundTag createNBT(String name, int amount) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("FluidName", name);
        nbt.putInt("Amount", amount);
        return nbt;
    }

}
