/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2026, Buuz135
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
package com.buuz135.industrial.plugin.jei;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.plugin.jei.category.BioReactorRecipeCategory;
import com.buuz135.industrial.plugin.jei.category.StoneWorkCategory;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceWrapper;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.utils.Reference;
import mezz.jei.api.recipe.RecipeType;

public class IndustrialRecipeTypes {

    public static RecipeType<BioReactorRecipeCategory.ReactorRecipeWrapper> BIOREACTOR = RecipeType.create(Reference.MOD_ID, "bioreactor", BioReactorRecipeCategory.ReactorRecipeWrapper.class);

    public static RecipeType<DissolutionChamberRecipe> DISSOLUTION = RecipeType.create(Reference.MOD_ID, "dissolution", DissolutionChamberRecipe.class);

    public static RecipeType<OreFluidEntryFermenter> FERMENTER = RecipeType.create(Reference.MOD_ID, "fermenter", OreFluidEntryFermenter.class);

    public static RecipeType<FluidExtractorRecipe> FLUID_EXTRACTOR = RecipeType.create(Reference.MOD_ID, "fluid_extractor", FluidExtractorRecipe.class);

    public static RecipeType<OreFluidEntrySieve> ORE_SIEVE = RecipeType.create(Reference.MOD_ID, "ore_sieve", OreFluidEntrySieve.class);

    public static RecipeType<LaserDrillOreRecipe> LASER_ORE = RecipeType.create(Reference.MOD_ID, "laser_ore", LaserDrillOreRecipe.class);

    public static RecipeType<LaserDrillFluidRecipe> LASER_FLUID = RecipeType.create(Reference.MOD_ID, "laser_fluid", LaserDrillFluidRecipe.class);

    public static RecipeType<OreFluidEntryRaw> ORE_WASHER = RecipeType.create(Reference.MOD_ID, "ore_washer", OreFluidEntryRaw.class);

    public static RecipeType<StoneWorkCategory.Wrapper> STONE_WORK = RecipeType.create(Reference.MOD_ID, "stone_work", StoneWorkCategory.Wrapper.class);

    public static RecipeType<StoneWorkGenerateRecipe> STONE_WORK_GENERATOR = RecipeType.create(Reference.MOD_ID, "stone_work_generator", StoneWorkGenerateRecipe.class);

    public static RecipeType<MachineProduceWrapper> MACHINE_PRODUCE = RecipeType.create(Reference.MOD_ID, "machine_produce", MachineProduceWrapper.class);
}
