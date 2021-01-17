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


import com.buuz135.industrial.block.generator.MycelialGeneratorBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.generator.tile.BioReactorTile;
import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.gui.conveyor.GuiConveyor;
import com.buuz135.industrial.module.*;
import com.buuz135.industrial.plugin.jei.category.*;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorCategory;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceCategory;
import com.buuz135.industrial.plugin.jei.machineproduce.MachineProduceWrapper;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.recipe.LaserDrillFluidRecipe;
import com.buuz135.industrial.recipe.LaserDrillOreRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.RecipeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

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

    public static void showUses(ItemStack stack) {
        //if (recipesGui != null && recipeRegistry != null)
        //    recipesGui.show(recipeRegistry.createFocus(IFocus.Mode.INPUT, stack));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModuleTool.INFINITY_DRILL, ModuleTool.INFINITY_SAW, ModuleTool.INFINITY_HAMMER, ModuleTool.INFINITY_TRIDENT, ModuleTool.INFINITY_BACKPACK, ModuleCore.EFFICIENCY_ADDON_1, ModuleCore.EFFICIENCY_ADDON_2, ModuleCore.SPEED_ADDON_1, ModuleCore.SPEED_ADDON_2);
        registration.useNbtForSubtypes(ModuleCore.RANGE_ADDONS);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(GuiConveyor.class, new IGhostIngredientHandler<GuiConveyor>() {
            @Override
            public <I> List<Target<I>> getTargets(GuiConveyor guiConveyor, I i, boolean b) {
                if (i instanceof ItemStack) {
                    return guiConveyor.getGhostSlots().stream().map(ghostSlot -> new Target<I>() {

                        @Override
                        public Rectangle2d getArea() {
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
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, FluidExtractorRecipe.SERIALIZER.getRecipeType()), fluidExtractorCategory.getUid());
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, DissolutionChamberRecipe.SERIALIZER.getRecipeType()), dissolutionChamberJEICategory.getUid());
        registration.addRecipes(generateBioreactorRecipes(), bioReactorRecipeCategory.getUid());
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, LaserDrillOreRecipe.SERIALIZER.getRecipeType()).stream().filter(laserDrillOreRecipe -> !laserDrillOreRecipe.output.hasNoMatchingItems()).collect(Collectors.toList()), laserRecipeOreCategory.getUid());
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, LaserDrillFluidRecipe.SERIALIZER.getRecipeType()), laserDrillFluidCategory.getUid());
        for (int i = 0; i < IMycelialGeneratorType.TYPES.size(); i++) {
            registration.addRecipes(IMycelialGeneratorType.TYPES.get(i).getRecipes().stream().sorted(Comparator.comparingInt(value -> ((MycelialGeneratorRecipe)value).getTicks() * ((MycelialGeneratorRecipe)value).getPowerTick()).reversed()).collect(Collectors.toList()), mycelialGeneratorCategories.get(i).getUid());
        }

        List<StoneWorkCategory.Wrapper> perfectStoneWorkWrappers = new ArrayList<>();
        for (MaterialStoneWorkFactoryTile.GeneratorRecipe generatorRecipe : MaterialStoneWorkFactoryTile.GENERATOR_RECIPES) {
            List<StoneWorkCategory.Wrapper> wrappers = findAllStoneWorkOutputs(generatorRecipe.getOutput(), new ArrayList<>());
            for (StoneWorkCategory.Wrapper workWrapper : new ArrayList<>(wrappers)) {
                if (perfectStoneWorkWrappers.stream().noneMatch(stoneWorkWrapper -> workWrapper.getOutput().isItemEqual(stoneWorkWrapper.getOutput()))) {
                    boolean isSomoneShorter = false;
                    for (StoneWorkCategory.Wrapper workWrapperCompare : new ArrayList<>(wrappers)) {
                        if (workWrapper.getOutput().isItemEqual(workWrapperCompare.getOutput())) {
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
            perfectStoneWorkWrappers.add(new StoneWorkCategory.Wrapper(generatorRecipe.getOutput(), new ArrayList<>(), generatorRecipe.getOutput()));
        }
        registration.addRecipes(perfectStoneWorkWrappers, stoneWorkCategory.getUid());

        registration.addRecipes(Arrays.asList(
                new MachineProduceWrapper(ModuleCore.LATEX_PROCESSING, new ItemStack(ModuleCore.TINY_DRY_RUBBER)),
                new MachineProduceWrapper(ModuleResourceProduction.SLUDGE_REFINER, IndustrialTags.Items.SLUDGE_OUTPUT),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER, new ItemStack(ModuleCore.FERTILIZER)),
                new MachineProduceWrapper(ModuleResourceProduction.DYE_MIXER, Tags.Items.DYES),
                new MachineProduceWrapper(ModuleResourceProduction.SPORES_RECREATOR, Tags.Items.MUSHROOMS),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.MOB_CRUSHER, new FluidStack(ModuleCore.ESSENCE.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY, new FluidStack(ModuleCore.MEAT.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY, new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.ANIMAL_RANCHER, new FluidStack(ModuleCore.MILK.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.SEWER, new FluidStack(ModuleCore.SEWAGE.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleAgricultureHusbandry.PLANT_GATHERER, new FluidStack(ModuleCore.SLUDGE.getSourceFluid(),  1000)),
                new MachineProduceWrapper(ModuleResourceProduction.WATER_CONDENSATOR, new FluidStack(Fluids.WATER,  1000))
        ), machineProduceCategory.getUid());


    }

    private List<BioReactorRecipeCategory.ReactorRecipeWrapper> generateBioreactorRecipes() {
        List<BioReactorRecipeCategory.ReactorRecipeWrapper> recipes = new ArrayList<>();
        for (ITag<Item> itemTag : BioReactorTile.VALID) {
            recipes.add(new BioReactorRecipeCategory.ReactorRecipeWrapper(itemTag, new FluidStack(ModuleCore.BIOFUEL.getSourceFluid(), 80)));
        }
        return recipes;
    }

    public ItemStack getStoneWorkOutputFrom(ItemStack stack, MaterialStoneWorkFactoryTile.StoneWorkAction mode) {
        return mode.getWork().apply(Minecraft.getInstance().world, ItemHandlerHelper.copyStackWithSize(stack, 9));
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
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.FLUID_EXTRACTOR), FluidExtractorCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleCore.DISSOLUTION_CHAMBER), DissolutionChamberCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleGenerator.BIOREACTOR), BioReactorRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.ORE_LASER_BASE), LaserDrillOreCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL), LaserDrillOreCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.FLUID_LASER_BASE), LaserDrillFluidCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.LASER_DRILL), LaserDrillFluidCategory.ID);
        for (MycelialGeneratorBlock mycelialGenerator : ModuleGenerator.MYCELIAL_GENERATORS) {
            for (MycelialGeneratorCategory mycelialGeneratorCategory : mycelialGeneratorCategories) {
                if (mycelialGenerator.getType().equals(mycelialGeneratorCategory.getType())){
                    registration.addRecipeCatalyst(new ItemStack(mycelialGenerator), mycelialGeneratorCategory.getUid());
                }
            }
        }
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY), stoneWorkCategory.getUid());
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.POTION_BREWER), VanillaRecipeCategoryUid.BREWING);
        registration.addRecipeCatalyst(new ItemStack(ModuleMisc.ENCHANTMENT_APPLICATOR), VanillaRecipeCategoryUid.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModuleResourceProduction.RESOURCEFUL_FURNACE), VanillaRecipeCategoryUid.FURNACE);
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
