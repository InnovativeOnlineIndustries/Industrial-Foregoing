/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.jei;


import com.buuz135.industrial.jei.extractor.FluidExtractorCategory;
import com.buuz135.industrial.jei.fluiddictionary.FluidDictionaryCategory;
import com.buuz135.industrial.jei.laser.LaserRecipeCategory;
import com.buuz135.industrial.jei.machineproduce.MachineProduceCategory;
import com.buuz135.industrial.jei.ore.OreFermenterCategory;
import com.buuz135.industrial.jei.ore.OreSieveCategory;
import com.buuz135.industrial.jei.ore.OreWasherCategory;
import com.buuz135.industrial.jei.petrifiedgen.PetrifiedBurnTimeCategory;
import com.buuz135.industrial.jei.reactor.ReactorRecipeCategory;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeCategory;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.recipe.FluidExtractorRecipe;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.RecipeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


@JeiPlugin
public class JEICustomPlugin implements IModPlugin {

    private static IRecipesGui recipesGui;
    private SludgeRefinerRecipeCategory sludgeRefinerRecipeCategory;
    private ReactorRecipeCategory bioReactorRecipeCategory;
    private ReactorRecipeCategory proteinReactorRecipeCategory;
    private LaserRecipeCategory laserRecipeCategory;
    private MachineProduceCategory machineProduceCategory;
    private PetrifiedBurnTimeCategory petrifiedBurnTimeCategory;
    private FluidDictionaryCategory fluidDictionaryCategory;
    private FluidExtractorCategory fluidExtractorCategory;
    private OreWasherCategory oreWasherCategory;
    private OreFermenterCategory oreFermenterCategory;
    private OreSieveCategory oreSieveCategory;
    private DissolutionChamberJEICategory dissolutionChamberJEICategory;

    public static void showUses(ItemStack stack) {
        //if (recipesGui != null && recipeRegistry != null)
        //    recipesGui.show(recipeRegistry.createFocus(IFocus.Mode.INPUT, stack));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModuleTool.INFINITY_DRILL);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
//        if (BlockRegistry.sludgeRefinerBlock.isEnabled()) {
//            sludgeRefinerRecipeCategory = new SludgeRefinerRecipeCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(sludgeRefinerRecipeCategory);
//        }
//        if (BlockRegistry.bioReactorBlock.isEnabled()) {
//            bioReactorRecipeCategory = new ReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper(), "Bioreactor accepted items");
//            registry.addRecipeCategories(bioReactorRecipeCategory);
//        }
//        if (BlockRegistry.proteinReactorBlock.isEnabled()) {
//            proteinReactorRecipeCategory = new ReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper(), "Protein reactor accepted items");
//            registry.addRecipeCategories(proteinReactorRecipeCategory);
//        }
//        if (BlockRegistry.laserBaseBlock.isEnabled() || BlockRegistry.laserDrillBlock.isEnabled()) {
//            laserRecipeCategory = new LaserRecipeCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(laserRecipeCategory);
//        }
//        machineProduceCategory = new MachineProduceCategory(registry.getJeiHelpers().getGuiHelper());
//        registry.addRecipeCategories(machineProduceCategory);
//        if (BlockRegistry.petrifiedFuelGeneratorBlock.isEnabled()) {
//            petrifiedBurnTimeCategory = new PetrifiedBurnTimeCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(petrifiedBurnTimeCategory);
//        }
//        if (BlockRegistry.fluidDictionaryConverterBlock.isEnabled() && !FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.isEmpty()) {
//            fluidDictionaryCategory = new FluidDictionaryCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(fluidDictionaryCategory);
//        }
//        if (BlockRegistry.materialStoneWorkFactoryBlock.isEnabled()) {
//            stoneWorkCategory = new StoneWorkCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(stoneWorkCategory);
//        }
//        if (BlockRegistry.treeFluidExtractorBlock.isEnabled()) {
        fluidExtractorCategory = new FluidExtractorCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(fluidExtractorCategory);
//        }
//        if (CustomConfiguration.enableBookEntriesInJEI) {
//            manualCategory = new ManualCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(manualCategory);
//        }
//        if (BlockRegistry.oreWasherBlock.isEnabled()) {
//            oreWasherCategory = new OreWasherCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(oreWasherCategory);
//        }
//        if (BlockRegistry.oreFermenterBlock.isEnabled()) {
//            oreFermenterCategory = new OreFermenterCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(oreFermenterCategory);
//        }
//        if (BlockRegistry.oreSieveBlock.isEnabled()) {
//            oreSieveCategory = new OreSieveCategory(registry.getJeiHelpers().getGuiHelper());
//            registry.addRecipeCategories(oreSieveCategory);
//        }
        dissolutionChamberJEICategory = new DissolutionChamberJEICategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(dissolutionChamberJEICategory);
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, FluidExtractorRecipe.SERIALIZER.getRecipeType()), fluidExtractorCategory.getUid());
        registration.addRecipes(RecipeUtil.getRecipes(Minecraft.getInstance().world, DissolutionChamberRecipe.SERIALIZER.getRecipeType()), dissolutionChamberJEICategory.getUid());
    }

    //@Override
    //public void register(IModRegistry registry) {
//        if (BlockRegistry.sludgeRefinerBlock.isEnabled()) {
//            int maxWeight = WeightedRandom.getTotalWeight(BlockRegistry.sludgeRefinerBlock.getItems());
//            List<SludgeRefinerRecipeWrapper> wrapperList = new ArrayList<>();
//            BlockRegistry.sludgeRefinerBlock.getItems().forEach(itemStackWeightedItem -> wrapperList.add(new SludgeRefinerRecipeWrapper(itemStackWeightedItem, maxWeight)));
//            registry.addRecipes(wrapperList, sludgeRefinerRecipeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.sludgeRefinerBlock), sludgeRefinerRecipeCategory.getUid());
//        }
//        if (BlockRegistry.bioReactorBlock.isEnabled()) {
//            List<ReactorRecipeWrapper> bioreactor = new ArrayList<>();
//            BioReactorEntry.BIO_REACTOR_ENTRIES.forEach(entry -> bioreactor.add(new ReactorRecipeWrapper(entry.getStack(), FluidsRegistry.BIOFUEL, BlockRegistry.bioReactorBlock.getBaseAmount())));
//            registry.addRecipes(bioreactor, bioReactorRecipeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.bioReactorBlock), bioReactorRecipeCategory.getUid());
//        }
//        if (BlockRegistry.proteinReactorBlock.isEnabled()) {
//            List<ReactorRecipeWrapper> proteinreactor = new ArrayList<>();
//            ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.forEach(entry -> proteinreactor.add(new ReactorRecipeWrapper(entry.getStack(), FluidsRegistry.PROTEIN, BlockRegistry.proteinReactorBlock.getBaseAmount())));
//            registry.addRecipes(proteinreactor, proteinReactorRecipeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.proteinReactorBlock), proteinReactorRecipeCategory.getUid());
//        }
//        if (BlockRegistry.laserBaseBlock.isEnabled() || BlockRegistry.laserDrillBlock.isEnabled()) {
//            List<LaserRecipeWrapper> laserRecipeWrappers = new ArrayList<>();
//            LaserDrillEntry.LASER_DRILL_UNIQUE_VALUES.forEach(entry -> laserRecipeWrappers.add(new LaserRecipeWrapper(entry)));
//            registry.addRecipes(laserRecipeWrappers, laserRecipeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.laserDrillBlock), laserRecipeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.laserBaseBlock), laserRecipeCategory.getUid());
//        }
//        if (BlockRegistry.resourcefulFurnaceBlock.isEnabled())
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.resourcefulFurnaceBlock), VanillaRecipeCategoryUid.SMELTING);
//        if (BlockRegistry.potionEnervatorBlock.isEnabled())
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.potionEnervatorBlock), VanillaRecipeCategoryUid.BREWING);
//
//        registry.addRecipes(Stream.of(
//                new MachineProduceWrapper(BlockRegistry.sporesRecreatorBlock, new ItemStack(Blocks.BROWN_MUSHROOM)),
//                new MachineProduceWrapper(BlockRegistry.sporesRecreatorBlock, new ItemStack(Blocks.RED_MUSHROOM)),
//                new MachineProduceWrapper(BlockRegistry.sewageCompostSolidiferBlock, new ItemStack(ItemRegistry.fertilizer)),
//                new MachineProduceWrapper(BlockRegistry.dyeMixerBlock, new ItemStack(ItemRegistry.artificalDye, 1, OreDictionary.WILDCARD_VALUE)),
//                new MachineProduceWrapper(BlockRegistry.lavaFabricatorBlock, new ItemStack(Items.LAVA_BUCKET)),
//                new MachineProduceWrapper(BlockRegistry.waterResourcesCollectorBlock, new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE)),
//                new MachineProduceWrapper(BlockRegistry.mobRelocatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.ESSENCE, 1000))),
//                new MachineProduceWrapper(BlockRegistry.cropRecolectorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.SLUDGE, 1000))),
//                new MachineProduceWrapper(BlockRegistry.waterCondensatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.WATER, 1000))),
//                new MachineProduceWrapper(BlockRegistry.animalResourceHarvesterBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MILK, 1000))),
//                new MachineProduceWrapper(BlockRegistry.mobSlaughterFactoryBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MEAT, 1000))),
//                new MachineProduceWrapper(BlockRegistry.mobSlaughterFactoryBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.PINK_SLIME, 1000))),
//                new MachineProduceWrapper(BlockRegistry.latexProcessingUnitBlock, new ItemStack(ItemRegistry.tinyDryRubber)),
//                new MachineProduceWrapper(BlockRegistry.animalByproductRecolectorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.SEWAGE, 1000))),
//                new MachineProduceWrapper(BlockRegistry.lavaFabricatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.LAVA, 1000))),
//                new MachineProduceWrapper(BlockRegistry.proteinReactorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.PROTEIN, 1000))),
//                new MachineProduceWrapper(BlockRegistry.frosterBlock, new ItemStack(Items.SNOWBALL)),
//                new MachineProduceWrapper(BlockRegistry.frosterBlock, new ItemStack(Blocks.ICE)),
//                new MachineProduceWrapper(BlockRegistry.frosterBlock, new ItemStack(Blocks.PACKED_ICE))
//                ).filter(machineProduceWrapper -> ((CustomOrientedBlock) machineProduceWrapper.getBlock()).isEnabled()).collect(Collectors.toList()),
//                machineProduceCategory.getUid());
//
//        if (BlockRegistry.materialStoneWorkFactoryBlock.isEnabled()) {
//            List<StoneWorkWrapper> perfectStoneWorkWrappers = new ArrayList<>();
//            List<StoneWorkWrapper> wrappers = findAllStoneWorkOutputs(new ArrayList<>());
//            for (StoneWorkWrapper workWrapper : new ArrayList<>(wrappers)) {
//                if (perfectStoneWorkWrappers.stream().noneMatch(stoneWorkWrapper -> workWrapper.getOutput().isItemEqual(stoneWorkWrapper.getOutput()))) {
//                    boolean isSomoneShorter = false;
//                    for (StoneWorkWrapper workWrapperCompare : new ArrayList<>(wrappers)) {
//                        if (workWrapper.getOutput().isItemEqual(workWrapperCompare.getOutput())) {
//                            List<MaterialStoneWorkFactoryTile.Mode> workWrapperCompareModes = new ArrayList<>(workWrapperCompare.getModes());
//                            workWrapperCompareModes.removeIf(mode -> mode == MaterialStoneWorkFactoryTile.Mode.NONE);
//                            List<MaterialStoneWorkFactoryTile.Mode> workWrapperModes = new ArrayList<>(workWrapper.getModes());
//                            workWrapperModes.removeIf(mode -> mode == MaterialStoneWorkFactoryTile.Mode.NONE);
//                            if (workWrapperModes.size() > workWrapperCompareModes.size()) {
//                                isSomoneShorter = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (!isSomoneShorter) perfectStoneWorkWrappers.add(workWrapper);
//                }
//            }
//            registry.addRecipes(perfectStoneWorkWrappers, stoneWorkCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.materialStoneWorkFactoryBlock), stoneWorkCategory.getUid());
//        }
//        if (BlockRegistry.petrifiedFuelGeneratorBlock.isEnabled()) {
//            List<PetrifiedBurnTimeWrapper> petrifiedBurnTimeWrappers = new ArrayList<>();
//            registry.getIngredientRegistry().getFuels().stream().filter(PetrifiedFuelGeneratorTile::acceptsInputStack).forEach(stack -> petrifiedBurnTimeWrappers.add(new PetrifiedBurnTimeWrapper(stack, TileEntityFurnace.getItemBurnTime(stack))));
//            registry.addRecipes(petrifiedBurnTimeWrappers, petrifiedBurnTimeCategory.getUid());
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock), petrifiedBurnTimeCategory.getUid());
//        }
//        if (CustomConfiguration.enableBookEntriesInJEI) {
//            for (BookCategory category : BookCategory.values()) {
//                registry.addRecipes(category.getEntries().values().stream().map(ManualWrapper::new).collect(Collectors.toList()), manualCategory.getUid());
//            }
//            registry.addRecipeCatalyst(new ItemStack(ItemRegistry.bookManualItem), manualCategory.getUid());
//        }
//        if (fluidDictionaryCategory != null) {
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.fluidDictionaryConverterBlock), fluidDictionaryCategory.getUid());
//            registry.addRecipes(FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.stream().map(FluidDictionaryWrapper::new).collect(Collectors.toList()), fluidDictionaryCategory.getUid());
//        }
//        if (extractorRecipeCategory != null) {
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.treeFluidExtractorBlock), extractorRecipeCategory.getUid());
//            registry.addRecipes(ExtractorEntry.EXTRACTOR_ENTRIES.stream().map(ExtractorRecipeWrapper::new).collect(Collectors.toList()), extractorRecipeCategory.getUid());
//        }
//        if (oreWasherCategory != null) {
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.oreWasherBlock), oreWasherCategory.getUid());
//            registry.addRecipes(OreFluidEntryRaw.ORE_RAW_ENTRIES.stream().map(OreWasherWrapper::new).collect(Collectors.toList()), oreWasherCategory.getUid());
//        }
//        if (oreFermenterCategory != null) {
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.oreFermenterBlock), oreFermenterCategory.getUid());
//            registry.addRecipes(OreFluidEntryFermenter.ORE_FLUID_FERMENTER.stream().map(OreFermenterWrapper::new).collect(Collectors.toList()), oreFermenterCategory.getUid());
//        }
//        if (oreSieveCategory != null) {
//            registry.addRecipeCatalyst(new ItemStack(BlockRegistry.oreSieveBlock), oreSieveCategory.getUid());
//            registry.addRecipes(OreFluidEntrySieve.ORE_FLUID_SIEVE.stream().map(OreSieveWrapper::new).collect(Collectors.toList()), oreSieveCategory.getUid());
//        }
//        registry.addGhostIngredientHandler(GuiConveyor.class, new ConveyorGhostSlotHandler());
    //}


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        //registration.addRecipeCatalyst(new ItemStack(ModuleCore.FLUID_EXTRACTOR), new ResourceLocation[]{fluidDictionaryCategory.getUid()});
        //registration.addRecipeCatalyst(new ItemStack(ModuleCore.DISSOLUTION_CHAMBER), new ResourceLocation[]{dissolutionChamberJEICategory.getUid()});
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
