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
package com.buuz135.industrial.plugin.jei;


import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.block.generator.MycelialGeneratorBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import com.buuz135.industrial.module.*;
import com.buuz135.industrial.plugin.RecipeViewerHelper;
import com.buuz135.industrial.plugin.jei.category.*;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorCategory;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceCategory;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceWrapper;
import com.buuz135.industrial.plugin.jei.subtype.AddonSubtypeInterpreter;
import com.buuz135.industrial.plugin.jei.subtype.InfinitySubtypeInterpreter;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.util.RecipeUtil;
import com.hrznstudio.titanium.util.TagUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.buuz135.industrial.plugin.RecipeViewerHelper.generateBioreactorRecipes;


@JeiPlugin
public class JEICustomPlugin implements IModPlugin {

    private static IRecipesGui recipesGui;
    private BioReactorRecipeCategory bioReactorRecipeCategory;
    private LaserDrillOreCategory laserRecipeOreCategory;
    private LaserDrillFluidCategory laserDrillFluidCategory;
    private FluidExtractorCategory fluidExtractorCategory;
    private DissolutionChamberCategory dissolutionChamberJEICategory;
    private List<MycelialGeneratorCategory> mycelialGeneratorCategories;
    private StoneWorkCategory stoneWorkCategory;
    private MachineProduceCategory machineProduceCategory;
    private StoneWorkGeneratorCategory stoneWorkGeneratorCategory;
    private OreWasherCategory oreWasherCategory;
    private FermentationStationCategory fermentationStationCategory;
    private FluidSieveCategory fluidSieveCategory;

    public static void showUses(ItemStack stack) {
        //if (recipesGui != null && recipeRegistry != null)
        //    recipesGui.show(recipeRegistry.createFocus(IFocus.Mode.INPUT, stack));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        List.of(ModuleTool.INFINITY_DRILL.get(), ModuleTool.INFINITY_SAW.get(), ModuleTool.INFINITY_HAMMER.get(), ModuleTool.INFINITY_TRIDENT.get(), ModuleTool.INFINITY_BACKPACK.get(), ModuleTool.INFINITY_LAUNCHER.get(), ModuleTool.INFINITY_NUKE.get())
                .forEach(item -> registration.registerSubtypeInterpreter(item, new InfinitySubtypeInterpreter()));
        Arrays.stream(ModuleCore.RANGE_ADDONS).forEach(itemItemDeferredHolder -> registration.registerSubtypeInterpreter(itemItemDeferredHolder.get(), new AddonSubtypeInterpreter()));
        List.of(ModuleCore.EFFICIENCY_ADDON_1.get(), ModuleCore.EFFICIENCY_ADDON_2.get(), ModuleCore.SPEED_ADDON_1.get(), ModuleCore.SPEED_ADDON_2.get(), ModuleCore.PROCESSING_ADDON_1.get())
                .forEach(item -> registration.registerSubtypeInterpreter(item, new AddonSubtypeInterpreter()));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(GuiConveyor.class, new IGhostIngredientHandler<>() {
            @Override
            public <I> List<Target<I>> getTargetsTyped(GuiConveyor guiConveyor, ITypedIngredient<I> i, boolean b) {
                if (i.getIngredient() instanceof ItemStack) {
                    return guiConveyor.getGhostSlots().stream().map(ghostSlot -> new Target<I>() {

                        @Override
                        public Rect2i getArea() {
                            return ghostSlot.getArea();
                        }

                        @Override
                        public void accept(I stack) {
                            ghostSlot.accept((ItemStack) stack);
                        }
                    }).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            @Override
            public void onComplete() {

            }
        });
        registration.addGhostIngredientHandler(GuiTransporter.class, new IGhostIngredientHandler<>() {
            @Override
            public <I> List<Target<I>> getTargetsTyped(GuiTransporter guiConveyor, ITypedIngredient<I> i, boolean b) {
                if (i.getIngredient() instanceof ItemStack) {
                    return guiConveyor.getGhostSlots().stream().map(ghostSlot -> new Target<I>() {

                        @Override
                        public Rect2i getArea() {
                            return ghostSlot.getArea();
                        }

                        @Override
                        public void accept(I stack) {
                            ghostSlot.accept((ItemStack) stack);
                        }
                    }).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        bioReactorRecipeCategory = new BioReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper(), Component.translatable("text.industrialforegoing.jei.recipe.title.bioreactor").getString());
        registry.addRecipeCategories(bioReactorRecipeCategory);
        fluidExtractorCategory = new FluidExtractorCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(fluidExtractorCategory);
        dissolutionChamberJEICategory = new DissolutionChamberCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(dissolutionChamberJEICategory);
        laserRecipeOreCategory = new LaserDrillOreCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(laserRecipeOreCategory);
        laserDrillFluidCategory = new LaserDrillFluidCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(laserDrillFluidCategory);
        mycelialGeneratorCategories = new ArrayList<>();
        for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
            MycelialGeneratorCategory category = new MycelialGeneratorCategory(type, registry.getJeiHelpers().getGuiHelper());
            mycelialGeneratorCategories.add(category);
            registry.addRecipeCategories(category);
        }
        stoneWorkCategory = new StoneWorkCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(stoneWorkCategory);
        machineProduceCategory = new MachineProduceCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(machineProduceCategory);
        stoneWorkGeneratorCategory = new StoneWorkGeneratorCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(stoneWorkGeneratorCategory);
        oreWasherCategory = new OreWasherCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(oreWasherCategory);
        fermentationStationCategory = new FermentationStationCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(fermentationStationCategory);
        fluidSieveCategory = new FluidSieveCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(fluidSieveCategory);
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(IndustrialRecipeTypes.FLUID_EXTRACTOR, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<FluidExtractorRecipe>) ModuleCore.FLUID_EXTRACTOR_TYPE.get()));
        registration.addRecipes(IndustrialRecipeTypes.DISSOLUTION, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<DissolutionChamberRecipe>) ModuleCore.DISSOLUTION_TYPE.get()));
        registration.addRecipes(IndustrialRecipeTypes.BIOREACTOR, generateBioreactorRecipes());
        registration.addRecipes(IndustrialRecipeTypes.LASER_ORE, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<LaserDrillOreRecipe>) ModuleCore.LASER_DRILL_TYPE.get()).stream().filter(laserDrillOreRecipe -> !laserDrillOreRecipe.output.isEmpty()).collect(Collectors.toList()));
        registration.addRecipes(IndustrialRecipeTypes.LASER_FLUID, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<LaserDrillFluidRecipe>) ModuleCore.LASER_DRILL_FLUID_TYPE.get()));
        for (int i = 0; i < IMycelialGeneratorType.TYPES.size(); i++) {
            registration.addRecipes(mycelialGeneratorCategories.get(i).getRecipeType(), IMycelialGeneratorType.TYPES.get(i).getRecipes(IFAttachments.registryAccess()).stream().sorted(Comparator.comparingInt(value -> ((MycelialGeneratorRecipe) value).getTicks() * ((MycelialGeneratorRecipe) value).getPowerTick()).reversed()).collect(Collectors.toList()));
        }


        registration.addRecipes(IndustrialRecipeTypes.STONE_WORK, RecipeViewerHelper.getStoneWork());

        registration.addRecipes(
                machineProduceCategory.getRecipeType(),
                Arrays.asList(
                        new MachineProduceWrapper(ModuleCore.LATEX_PROCESSING.getBlock(), new ItemStack(ModuleCore.DRY_RUBBER.get())),
                        new MachineProduceWrapper(ModuleResourceProduction.SLUDGE_REFINER.getBlock(), IndustrialTags.Items.SLUDGE_OUTPUT),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER.getBlock(), new ItemStack(ModuleCore.FERTILIZER.get())),
                        new MachineProduceWrapper(ModuleResourceProduction.DYE_MIXER.getBlock(), Tags.Items.DYES),
                        new MachineProduceWrapper(ModuleResourceProduction.SPORES_RECREATOR.getBlock(), Tags.Items.MUSHROOMS),
                        new MachineProduceWrapper(ModuleResourceProduction.SPORES_RECREATOR.getBlock(), new ItemStack(Items.CRIMSON_FUNGUS), new ItemStack(Items.WARPED_FUNGUS)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.MOB_CRUSHER.getBlock(), new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY.getBlock(), new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY.getBlock(), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.ANIMAL_RANCHER.getBlock(), new FluidStack(NeoForgeMod.MILK.get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWER.getBlock(), new FluidStack(ModuleCore.SEWAGE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.PLANT_GATHERER.getBlock(), new FluidStack(ModuleCore.SLUDGE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleResourceProduction.WATER_CONDENSATOR.getBlock(), new FluidStack(Fluids.WATER, 1000))
                )
        );

        registration.addRecipes(IndustrialRecipeTypes.STONE_WORK_GENERATOR, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<StoneWorkGenerateRecipe>) ModuleCore.STONEWORK_GENERATE_TYPE.get()));

        List<OreFluidEntryRaw> washer = new ArrayList<>();
        List<OreFluidEntryFermenter> fluidEntryFermenters = new ArrayList<>();
        List<OreFluidEntrySieve> fluidSieve = new ArrayList<>();

        BuiltInRegistries.ITEM.getTagNames().map(itemTagKey -> itemTagKey.location())
                .filter(resourceLocation -> resourceLocation.toString().startsWith("c:raw_materials/") && OreTitaniumFluidType.isValid(resourceLocation))
                .forEach(resourceLocation -> {
                    TagKey<Item> tag = TagUtil.getItemTag(resourceLocation);
                    TagKey<Item> dust = TagUtil.getItemTag(ResourceLocation.parse(resourceLocation.toString().replace("c:raw_materials/", "c:dusts/")));
                    washer.add(new OreFluidEntryRaw(tag, new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), 100), OreTitaniumFluidType.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation)));
                    fluidEntryFermenters.add(new OreFluidEntryFermenter(OreTitaniumFluidType.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation), OreTitaniumFluidType.getFluidWithTag(ModuleCore.FERMENTED_ORE_MEAT, 200, resourceLocation)));
                    fluidSieve.add(new OreFluidEntrySieve(OreTitaniumFluidType.getFluidWithTag(ModuleCore.FERMENTED_ORE_MEAT, 100, resourceLocation), TagUtil.getItemWithPreference(dust), ItemTags.SAND));
                });
        registration.addRecipes(IndustrialRecipeTypes.ORE_WASHER, washer);
        registration.addRecipes(IndustrialRecipeTypes.FERMENTER, fluidEntryFermenters);
        registration.addRecipes(IndustrialRecipeTypes.ORE_SIEVE, fluidSieve);
    }



    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.FLUID_EXTRACTOR.getBlock()), IndustrialRecipeTypes.FLUID_EXTRACTOR);
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.DISSOLUTION_CHAMBER.getBlock()), IndustrialRecipeTypes.DISSOLUTION);
        registration.addRecipeCatalyst(new ItemStack(ModuleGenerator.BIOREACTOR.getBlock()), IndustrialRecipeTypes.BIOREACTOR);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.ORE_LASER_BASE.getBlock()), IndustrialRecipeTypes.LASER_ORE);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL.getBlock()), IndustrialRecipeTypes.LASER_ORE);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FLUID_LASER_BASE.getBlock()), IndustrialRecipeTypes.LASER_FLUID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL.getBlock()), IndustrialRecipeTypes.LASER_FLUID);
        for (BlockWithTile mycelialGenerator : ModuleGenerator.MYCELIAL_GENERATORS) {
            for (MycelialGeneratorCategory mycelialGeneratorCategory : mycelialGeneratorCategories) {
                if (((MycelialGeneratorBlock) mycelialGenerator.getBlock()).getType().equals(mycelialGeneratorCategory.getType())) {
                    registration.addRecipeCatalyst(new ItemStack(mycelialGenerator.getBlock()), mycelialGeneratorCategory.getRecipeType());
                }
            }
        }
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.getBlock()), stoneWorkCategory.getRecipeType(), stoneWorkGeneratorCategory.getRecipeType());
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.POTION_BREWER.getBlock()), RecipeTypes.BREWING);
        registration.addRecipeCatalyst(new ItemStack(ModuleMisc.ENCHANTMENT_APPLICATOR.getBlock()), RecipeTypes.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.RESOURCEFUL_FURNACE.getBlock()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.WASHING_FACTORY.getBlock()), IndustrialRecipeTypes.ORE_WASHER);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FERMENTATION_STATION.getBlock()), IndustrialRecipeTypes.FERMENTER);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FLUID_SIEVING_MACHINE.getBlock()), IndustrialRecipeTypes.ORE_SIEVE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        //recipesGui = jeiRuntime.getRecipesGui();
        //recipeRegistry = jeiRuntime.getRecipeRegistry();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "default");
    }
}
