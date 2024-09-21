package com.buuz135.industrial.plugin.jei;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.plugin.jei.category.BioReactorRecipeCategory;
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

    public static RecipeType<StoneWorkWrapper> STONE_WORK = RecipeType.create(Reference.MOD_ID, "stone_work", StoneWorkWrapper.class);

    public static RecipeType<StoneWorkGenerateRecipe> STONE_WORK_GENERATOR = RecipeType.create(Reference.MOD_ID, "stone_work_generator", StoneWorkGenerateRecipe.class);

    public static RecipeType<MachineProduceWrapper> MACHINE_PRODUCE = RecipeType.create(Reference.MOD_ID, "machine_produce", MachineProduceWrapper.class);
}
