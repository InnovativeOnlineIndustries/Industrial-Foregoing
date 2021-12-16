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
package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.recipe.generator.IJSONGenerator;
import com.hrznstudio.titanium.recipe.generator.IJsonFile;
import com.hrznstudio.titanium.recipe.generator.TitaniumSerializableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class IndustrialSerializableProvider extends TitaniumSerializableProvider {

    public IndustrialSerializableProvider(DataGenerator generatorIn, String modid) {
        super(generatorIn, modid);
    }

    @Override
    public void add(Map<IJsonFile, IJSONGenerator> serializables) {
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "pink_slime_ball"), new Ingredient.Value[]{new Ingredient.ItemValue(new ItemStack(Items.GLASS_PANE))}, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 300), 200, new ItemStack(ModuleCore.PINK_SLIME_ITEM.get()), new FluidStack(Fluids.WATER, 150));
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "pink_slime_ingot"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                }, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 1000), 300, new ItemStack(ModuleCore.PINK_SLIME_INGOT.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "mechanical_dirt"),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Blocks.DIRT)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.DIRT)),
                        new Ingredient.ItemValue(new ItemStack(Items.ROTTEN_FLESH)),
                        new Ingredient.ItemValue(new ItemStack(Items.ROTTEN_FLESH)),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_PITY),
                }, new FluidStack(ModuleCore.MEAT.getSourceFluid(), 1000), 100, new ItemStack(ModuleResourceProduction.MECHANICAL_DIRT.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "simple_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_PITY),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_BRICK)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_BRICK)),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_GOLD),
                        new Ingredient.TagValue(Tags.Items.INGOTS_IRON)
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid(), 250), 300, new ItemStack(ModuleCore.SIMPLE.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "advanced_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_SIMPLE),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_SCRAP)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_SCRAP)),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.TagValue(Tags.Items.INGOTS_GOLD)
                },
                new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), 500), 300, new ItemStack(ModuleCore.ADVANCED.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "dark_glass"),
                new Ingredient.Value[]{
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                        new Ingredient.ItemValue(new ItemStack(Blocks.SOUL_SAND)),
                },
                new FluidStack(ModuleCore.LATEX.getSourceFluid(), 100), 100, new ItemStack(ModuleCore.DARK_GLASS.get(), 8), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "supreme_machine_frame"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_ADVANCED),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.TagValue(Tags.Items.GEMS_DIAMOND),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.TagValue(Tags.Items.GEMS_DIAMOND)
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 135), 300, new ItemStack(ModuleCore.SUPREME.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "mycelial_reactor"),
                new Ingredient.Value[]{
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.TagValue(IndustrialTags.Items.MACHINE_FRAME_SUPREME),
                        new Ingredient.TagValue(IndustrialTags.Items.PLASTIC),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHERITE_INGOT)),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND),
                        new Ingredient.ItemValue(new ItemStack(Items.NETHER_STAR)),
                        new Ingredient.TagValue(IndustrialTags.Items.GEAR_DIAMOND)
                },
                new FluidStack(ModuleCore.ETHER.getSourceFluid(), 500), 600, new ItemStack(ModuleGenerator.MYCELIAL_REACTOR.get()), FluidStack.EMPTY);
        new DissolutionChamberRecipe(new ResourceLocation(Reference.MOD_ID, "xp_bottles"),
                new Ingredient.Value[]{},
                new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), 250), 5, new ItemStack(Items.EXPERIENCE_BOTTLE), FluidStack.EMPTY);
        DissolutionChamberRecipe.RECIPES.forEach(dissolutionChamberRecipe -> serializables.put(dissolutionChamberRecipe, dissolutionChamberRecipe));
        FluidExtractorRecipe.RECIPES.forEach(fluidExtractorRecipe -> serializables.put(fluidExtractorRecipe, fluidExtractorRecipe));
        LaserDrillFluidRecipe.init();
        LaserDrillFluidRecipe.RECIPES.forEach(laserDrillFluidRecipe -> serializables.put(laserDrillFluidRecipe, laserDrillFluidRecipe));
        LaserDrillOreRecipe.init();
        LaserDrillOreRecipe.RECIPES.forEach(laserDrillOreRecipe -> serializables.put(laserDrillOreRecipe, laserDrillOreRecipe));
        StoneWorkGenerateRecipe.RECIPES.forEach(stoneWorkGenerateRecipe -> serializables.put(stoneWorkGenerateRecipe, stoneWorkGenerateRecipe));
        CrusherRecipe.init();
        CrusherRecipe.RECIPES.forEach(crusherRecipe -> serializables.put(crusherRecipe, crusherRecipe));
    }
}
