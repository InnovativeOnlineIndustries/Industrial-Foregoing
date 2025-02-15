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
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidExtractorRecipe implements Recipe<CraftingInput> {

    public static final MapCodec<FluidExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            Ingredient.CODEC.fieldOf("input").forGetter(o -> o.input),
            BlockState.CODEC.fieldOf("result").forGetter(o -> o.result),
            Codec.FLOAT.fieldOf("breakChance").forGetter(o -> o.breakChance),
            FluidStack.CODEC.fieldOf("output").forGetter(o -> o.output),
            Codec.BOOL.fieldOf("defaultRecipe").forGetter(o -> o.defaultRecipe)
    ).apply(in, FluidExtractorRecipe::new));

    public Ingredient input;
    public BlockState result;
    public float breakChance;
    public FluidStack output;
    public boolean defaultRecipe;

    public FluidExtractorRecipe(Ingredient input, BlockState result, float breakChance, FluidStack output, boolean defaultRecipe) {
        this.input = input;
        this.result = result;
        this.breakChance = breakChance;
        this.output = output;
        this.defaultRecipe = defaultRecipe;
    }
    public FluidExtractorRecipe() {
    }

    public static void init(RecipeOutput output) {
        createRecipe(output, "acacia", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.ACACIA_LOG)), Blocks.STRIPPED_ACACIA_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 4), false));
        createRecipe(output, "mangrove", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.MANGROVE_LOG)), Blocks.STRIPPED_MANGROVE_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 4), false));
        createRecipe(output, "dark_oak", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.DARK_OAK_LOG)), Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 3), false));
        createRecipe(output, "cherry", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.CHERRY_LOG)), Blocks.STRIPPED_CHERRY_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 3), false));
        createRecipe(output, "oak", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.OAK_LOG)), Blocks.STRIPPED_OAK_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false));
        createRecipe(output, "spruce", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.SPRUCE_LOG)), Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false));
        createRecipe(output, "birch", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.BIRCH_LOG)), Blocks.STRIPPED_BIRCH_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false));
        createRecipe(output, "jungle", new FluidExtractorRecipe(Ingredient.of(new ItemStack(Blocks.JUNGLE_LOG)), Blocks.STRIPPED_JUNGLE_LOG.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false));
        createRecipe(output, "default", new FluidExtractorRecipe(Ingredient.of(ItemTags.LOGS), Blocks.AIR.defaultBlockState(), 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 1), true));
    }

    public static void createRecipe(RecipeOutput recipeOutput, String name, FluidExtractorRecipe recipe) {
        var rl = generateRL(name);
        var advancementHolder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(rl))
                .rewards(AdvancementRewards.Builder.recipe(rl))
                .requirements(AdvancementRequirements.Strategy.OR).build(rl);
        recipeOutput.accept(rl, recipe, advancementHolder);
    }

    public static ResourceLocation generateRL(String key) {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_extractor/" + key);
    }

    public boolean outputsLatex() {
        return output.getFluid().isSame(ModuleCore.LATEX.getSourceFluid().get());
    }

    public boolean matches(Level world, BlockPos pos) {
        return input.test(new ItemStack(world.getBlockState(pos).getBlock()));
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
        return ModuleCore.FLUID_EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.FLUID_EXTRACTOR_TYPE.get();
    }


}
