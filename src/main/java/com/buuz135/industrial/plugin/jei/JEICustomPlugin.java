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
import com.buuz135.industrial.block.generator.tile.BioReactorTile;
import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.gui.transporter.GuiTransporter;
import com.buuz135.industrial.module.*;
import com.buuz135.industrial.plugin.jei.category.*;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorCategory;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceCategory;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceWrapper;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.RecipeUtil;
import com.hrznstudio.titanium.util.TagUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;


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
        registration.useNbtForSubtypes(ModuleTool.INFINITY_DRILL.get(), ModuleTool.INFINITY_SAW.get(), ModuleTool.INFINITY_HAMMER.get(), ModuleTool.INFINITY_TRIDENT.get(), ModuleTool.INFINITY_BACKPACK.get(), ModuleTool.INFINITY_LAUNCHER.get(), ModuleTool.INFINITY_NUKE.get(), ModuleCore.EFFICIENCY_ADDON_1.get(), ModuleCore.EFFICIENCY_ADDON_2.get(), ModuleCore.SPEED_ADDON_1.get(), ModuleCore.SPEED_ADDON_2.get(), ModuleCore.PROCESSING_ADDON_1.get());
        Arrays.stream(ModuleCore.RANGE_ADDONS).map(RegistryObject::get).forEach(registration::useNbtForSubtypes);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(GuiConveyor.class, new IGhostIngredientHandler<>() {
            @Override
            public <I> List<Target<I>> getTargets(GuiConveyor guiConveyor, I i, boolean b) {
                if (i instanceof ItemStack) {
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
            public <I> List<Target<I>> getTargets(GuiTransporter guiConveyor, I i, boolean b) {
                if (i instanceof ItemStack) {
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
        bioReactorRecipeCategory = new BioReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper(), "Bioreactor accepted items");
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
            registration.addRecipes(mycelialGeneratorCategories.get(i).getRecipeType(), IMycelialGeneratorType.TYPES.get(i).getRecipes().stream().sorted(Comparator.comparingInt(value -> ((MycelialGeneratorRecipe) value).getTicks() * ((MycelialGeneratorRecipe) value).getPowerTick()).reversed()).collect(Collectors.toList()));
        }

        List<StoneWorkCategory.Wrapper> perfectStoneWorkWrappers = new ArrayList<>();
        for (StoneWorkGenerateRecipe generatorRecipe : RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<StoneWorkGenerateRecipe>) ModuleCore.STONEWORK_GENERATE_TYPE.get())) {
            List<StoneWorkCategory.Wrapper> wrappers = findAllStoneWorkOutputs(generatorRecipe.output, new ArrayList<>());
            for (StoneWorkCategory.Wrapper workWrapper : new ArrayList<>(wrappers)) {
                if (perfectStoneWorkWrappers.stream().noneMatch(stoneWorkWrapper -> workWrapper.getOutput().sameItem(stoneWorkWrapper.getOutput()))) {
                    boolean isSomoneShorter = false;
                    for (StoneWorkCategory.Wrapper workWrapperCompare : new ArrayList<>(wrappers)) {
                        if (workWrapper.getOutput().sameItem(workWrapperCompare.getOutput())) {
                            List<MaterialStoneWorkFactoryTile.StoneWorkAction> workWrapperCompareModes = new ArrayList<>(workWrapperCompare.getModes());
                            workWrapperCompareModes.removeIf(mode -> mode.getAction().equalsIgnoreCase("none"));
                            List<MaterialStoneWorkFactoryTile.StoneWorkAction> workWrapperModes = new ArrayList<>(workWrapper.getModes());
                            workWrapperModes.removeIf(mode -> mode.getAction().equalsIgnoreCase("none"));
                            if (workWrapperModes.size() > workWrapperCompareModes.size()) {
                                isSomoneShorter = true;
                                break;
                            }
                        }
                    }
                    if (!isSomoneShorter) perfectStoneWorkWrappers.add(workWrapper);
                }
            }
        }
        registration.addRecipes(IndustrialRecipeTypes.STONE_WORK, perfectStoneWorkWrappers);

        registration.addRecipes(
                machineProduceCategory.getRecipeType(),
                Arrays.asList(
                        new MachineProduceWrapper(ModuleCore.LATEX_PROCESSING.getLeft().get(), new ItemStack(ModuleCore.TINY_DRY_RUBBER.get())),
                        new MachineProduceWrapper(ModuleResourceProduction.SLUDGE_REFINER.getLeft().get(), IndustrialTags.Items.SLUDGE_OUTPUT),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER.getLeft().get(), new ItemStack(ModuleCore.FERTILIZER.get())),
                        new MachineProduceWrapper(ModuleResourceProduction.DYE_MIXER.getLeft().get(), Tags.Items.DYES),
                        new MachineProduceWrapper(ModuleResourceProduction.SPORES_RECREATOR.getLeft().get(), Tags.Items.MUSHROOMS),
                        new MachineProduceWrapper(ModuleResourceProduction.SPORES_RECREATOR.getLeft().get(), new ItemStack(Items.CRIMSON_FUNGUS), new ItemStack(Items.WARPED_FUNGUS)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.MOB_CRUSHER.getLeft().get(), new FluidStack(ModuleCore.ESSENCE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY.getLeft().get(), new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY.getLeft().get(), new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.ANIMAL_RANCHER.getLeft().get(), new FluidStack(ForgeMod.MILK.get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWER.getLeft().get(), new FluidStack(ModuleCore.SEWAGE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleAgricultureHusbandry.PLANT_GATHERER.getLeft().get(), new FluidStack(ModuleCore.SLUDGE.getSourceFluid().get(), 1000)),
                        new MachineProduceWrapper(ModuleResourceProduction.WATER_CONDENSATOR.getLeft().get(), new FluidStack(Fluids.WATER, 1000))
                )
        );

        registration.addRecipes(IndustrialRecipeTypes.STONE_WORK_GENERATOR, RecipeUtil.getRecipes(Minecraft.getInstance().level, (RecipeType<StoneWorkGenerateRecipe>) ModuleCore.STONEWORK_GENERATE_TYPE.get()));

        List<OreFluidEntryRaw> washer = new ArrayList<>();
        List<OreFluidEntryFermenter> fluidEntryFermenters = new ArrayList<>();
        List<OreFluidEntrySieve> fluidSieve = new ArrayList<>();

        ForgeRegistries.ITEMS.tags().getTagNames().map(itemTagKey -> itemTagKey.location())
                .filter(resourceLocation -> resourceLocation.toString().startsWith("forge:raw_materials/") && OreTitaniumFluidType.isValid(resourceLocation))
                .forEach(resourceLocation -> {
                    TagKey<Item> tag = ForgeRegistries.ITEMS.tags().createTagKey(resourceLocation);
                    TagKey<Item> dust = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(resourceLocation.toString().replace("forge:raw_materials/", "forge:dusts/")));
                    washer.add(new OreFluidEntryRaw(tag, new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), 100), OreTitaniumFluidType.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation)));
                    fluidEntryFermenters.add(new OreFluidEntryFermenter(OreTitaniumFluidType.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation), OreTitaniumFluidType.getFluidWithTag(ModuleCore.FERMENTED_ORE_MEAT, 200, resourceLocation)));
                    fluidSieve.add(new OreFluidEntrySieve(OreTitaniumFluidType.getFluidWithTag(ModuleCore.FERMENTED_ORE_MEAT, 100, resourceLocation), TagUtil.getItemWithPreference(dust), ItemTags.SAND));
                });
        registration.addRecipes(IndustrialRecipeTypes.ORE_WASHER, washer);
        registration.addRecipes(IndustrialRecipeTypes.FERMENTER, fluidEntryFermenters);
        registration.addRecipes(IndustrialRecipeTypes.ORE_SIEVE, fluidSieve);
    }

    private List<BioReactorRecipeCategory.ReactorRecipeWrapper> generateBioreactorRecipes() {
        List<BioReactorRecipeCategory.ReactorRecipeWrapper> recipes = new ArrayList<>();
        for (TagKey<Item> itemTag : BioReactorTile.VALID) {
            recipes.add(new BioReactorRecipeCategory.ReactorRecipeWrapper(itemTag, new FluidStack(ModuleCore.BIOFUEL.getSourceFluid().get(), 80)));
        }
        return recipes;
    }

    public ItemStack getStoneWorkOutputFrom(ItemStack stack, MaterialStoneWorkFactoryTile.StoneWorkAction mode) {
        return mode.getWork().apply(Minecraft.getInstance().level, ItemHandlerHelper.copyStackWithSize(stack, 9));
    }

    public ItemStack getStoneWorkOutputFrom(ItemStack stack, List<MaterialStoneWorkFactoryTile.StoneWorkAction> modes) {
        for (MaterialStoneWorkFactoryTile.StoneWorkAction mode : modes) {
            stack = getStoneWorkOutputFrom(stack.copy(), mode);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    public List<StoneWorkCategory.Wrapper> findAllStoneWorkOutputs(ItemStack parent, List<MaterialStoneWorkFactoryTile.StoneWorkAction> usedModes) {
        List<StoneWorkCategory.Wrapper> wrappers = new ArrayList<>();
        if (usedModes.size() >= 4) return wrappers;
        for (MaterialStoneWorkFactoryTile.StoneWorkAction mode : MaterialStoneWorkFactoryTile.ACTION_RECIPES) {
            if (mode.getAction().equals("none")) continue;
            List<MaterialStoneWorkFactoryTile.StoneWorkAction> usedModesInternal = new ArrayList<>(usedModes);
            usedModesInternal.add(mode);
            ItemStack output = getStoneWorkOutputFrom(parent, new ArrayList<>(usedModesInternal));
            if (!output.isEmpty()) {
                wrappers.add(new StoneWorkCategory.Wrapper(parent, new ArrayList<>(usedModesInternal), output.copy()));
                wrappers.addAll(findAllStoneWorkOutputs(parent, new ArrayList<>(usedModesInternal)));
            }
        }
        return wrappers;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.FLUID_EXTRACTOR.getLeft().get()), IndustrialRecipeTypes.FLUID_EXTRACTOR);
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.DISSOLUTION_CHAMBER.getLeft().get()), IndustrialRecipeTypes.DISSOLUTION);
        registration.addRecipeCatalyst(new ItemStack(ModuleGenerator.BIOREACTOR.getLeft().get()), IndustrialRecipeTypes.BIOREACTOR);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.ORE_LASER_BASE.getLeft().get()), IndustrialRecipeTypes.LASER_ORE);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL.getLeft().get()), IndustrialRecipeTypes.LASER_ORE);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FLUID_LASER_BASE.getLeft().get()), IndustrialRecipeTypes.LASER_FLUID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL.getLeft().get()), IndustrialRecipeTypes.LASER_FLUID);
        for (Pair<RegistryObject<Block>, RegistryObject<BlockEntityType<?>>> mycelialGenerator : ModuleGenerator.MYCELIAL_GENERATORS) {
            for (MycelialGeneratorCategory mycelialGeneratorCategory : mycelialGeneratorCategories) {
                if (((MycelialGeneratorBlock) mycelialGenerator.getLeft().get()).getType().equals(mycelialGeneratorCategory.getType())) {
                    registration.addRecipeCatalyst(new ItemStack(mycelialGenerator.getLeft().get()), mycelialGeneratorCategory.getRecipeType());
                }
            }
        }
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.getLeft().get()), stoneWorkCategory.getRecipeType(), stoneWorkGeneratorCategory.getRecipeType());
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.POTION_BREWER.getLeft().get()), RecipeTypes.BREWING);
        registration.addRecipeCatalyst(new ItemStack(ModuleMisc.ENCHANTMENT_APPLICATOR.getLeft().get()), RecipeTypes.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.RESOURCEFUL_FURNACE.getLeft().get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.WASHING_FACTORY.getLeft().get()), IndustrialRecipeTypes.ORE_WASHER);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FERMENTATION_STATION.getLeft().get()), IndustrialRecipeTypes.FERMENTER);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FLUID_SIEVING_MACHINE.getLeft().get()), IndustrialRecipeTypes.ORE_SIEVE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        //recipesGui = jeiRuntime.getRecipesGui();
        //recipeRegistry = jeiRuntime.getRecipeRegistry();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Reference.MOD_ID, "default");
    }
}
