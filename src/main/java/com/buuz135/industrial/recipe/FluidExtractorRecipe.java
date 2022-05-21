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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FluidExtractorRecipe extends SerializableRecipe {

    public static GenericSerializer<FluidExtractorRecipe> SERIALIZER = new GenericSerializer<>(new ResourceLocation(Reference.MOD_ID, "fluid_extractor"), FluidExtractorRecipe.class);
    public static List<FluidExtractorRecipe> RECIPES = new ArrayList<>();

    static {
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "acacia"), new Ingredient.ItemValue(new ItemStack(Blocks.ACACIA_LOG)), Blocks.STRIPPED_ACACIA_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 4), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "dark_oak"), new Ingredient.ItemValue(new ItemStack(Blocks.DARK_OAK_LOG)), Blocks.STRIPPED_DARK_OAK_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 3), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "oak"), new Ingredient.ItemValue(new ItemStack(Blocks.OAK_LOG)), Blocks.STRIPPED_OAK_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "spruce"), new Ingredient.ItemValue(new ItemStack(Blocks.SPRUCE_LOG)), Blocks.STRIPPED_SPRUCE_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "birch"), new Ingredient.ItemValue(new ItemStack(Blocks.BIRCH_LOG)), Blocks.STRIPPED_BIRCH_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "jungle"), new Ingredient.ItemValue(new ItemStack(Blocks.JUNGLE_LOG)), Blocks.STRIPPED_JUNGLE_LOG, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 2), false);
        new FluidExtractorRecipe(new ResourceLocation(Reference.MOD_ID, "default"), new Ingredient.TagValue(ItemTags.LOGS), Blocks.AIR, 0.010f, new FluidStack(ModuleCore.LATEX.getSourceFluid().get(), 1), true);
    }

    public Ingredient.Value input;
    public Block result;
    public float breakChance;
    public FluidStack output;
    public boolean defaultRecipe;

    // see updateIngredient()
    private transient Ingredient ingredient;

    public FluidExtractorRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public FluidExtractorRecipe(ResourceLocation resourceLocation, Ingredient.Value input, Block result, float breakChance, FluidStack output, boolean defaultRecipe) {
        super(resourceLocation);
        this.input = input;
        this.result = result;
        this.breakChance = breakChance;
        this.output = output;
        this.defaultRecipe = defaultRecipe;
        RECIPES.add(this);
    }

    public boolean matches(Level world, BlockPos pos) {
        return getOrCacheInput().test(new ItemStack(world.getBlockState(pos).getBlock()));
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
        return input.getItems().iterator().next();
    }

    @Override
    public GenericSerializer<? extends SerializableRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return SERIALIZER.getRecipeType();
    }

    /**
     * This is used to cache the ingredient used in {@link #matches(Level, BlockPos)} in order to
     * avoid creating the same Ingredient each work tick, which could cause a massive performance hit.
     *
     * Note that this is not an optimal solution, as ideally, the recipe itself would use {@link Ingredient} directly,
     * however due to the way recipes are currently being created at static init, this would cause crashes with unbound
     * tags during Ingredient construction (specifically during Forge's isSimple check)
     */
    private Ingredient getOrCacheInput() {
        if(ingredient == null) {
            ingredient = Ingredient.fromValues(Stream.of(this.input));
        }
        return ingredient;
    }
}
