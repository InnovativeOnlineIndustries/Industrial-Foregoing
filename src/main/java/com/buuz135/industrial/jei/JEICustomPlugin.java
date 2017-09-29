package com.buuz135.industrial.jei;


import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.LaserDrillEntry;
import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeCategory;
import com.buuz135.industrial.jei.bioreactor.BioReactorRecipeWrapper;
import com.buuz135.industrial.jei.laser.LaserRecipeCategory;
import com.buuz135.industrial.jei.laser.LaserRecipeWrapper;
import com.buuz135.industrial.jei.machineproduce.MachineProduceCategory;
import com.buuz135.industrial.jei.machineproduce.MachineProduceWrapper;
import com.buuz135.industrial.jei.petrifiedgen.PetrifiedBurnTimeCategory;
import com.buuz135.industrial.jei.petrifiedgen.PetrifiedBurnTimeWrapper;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeCategory;
import com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeWrapper;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.world.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.ItemStackWeightedItem;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@JEIPlugin
public class JEICustomPlugin implements IModPlugin {

    private static IRecipesGui recipesGui;
    private static IRecipeRegistry recipeRegistry;
    private SludgeRefinerRecipeCategory sludgeRefinerRecipeCategory;
    private BioReactorRecipeCategory bioReactorRecipeCategory;
    private LaserRecipeCategory laserRecipeCategory;
    private MachineProduceCategory machineProduceCategory;
    private PetrifiedBurnTimeCategory petrifiedBurnTimeCategory;

    public static void showUses(ItemStack stack) {
        recipesGui.show(recipeRegistry.createFocus(IFocus.Mode.INPUT, stack));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        sludgeRefinerRecipeCategory = new SludgeRefinerRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(sludgeRefinerRecipeCategory);
        bioReactorRecipeCategory = new BioReactorRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(bioReactorRecipeCategory);
        laserRecipeCategory = new LaserRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(laserRecipeCategory);
        machineProduceCategory = new MachineProduceCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(machineProduceCategory);
        petrifiedBurnTimeCategory = new PetrifiedBurnTimeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(petrifiedBurnTimeCategory);
    }

    @Override
    public void register(IModRegistry registry) {
        int maxWeight = WeightedRandom.getTotalWeight(BlockRegistry.sludgeRefinerBlock.getItems());
        List<SludgeRefinerRecipeWrapper> wrapperList = new ArrayList<>();
        BlockRegistry.sludgeRefinerBlock.getItems().forEach(itemStackWeightedItem -> wrapperList.add(new SludgeRefinerRecipeWrapper(itemStackWeightedItem, maxWeight)));
        registry.addRecipes(wrapperList, sludgeRefinerRecipeCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.sludgeRefinerBlock), sludgeRefinerRecipeCategory.getUid());


        List<BioReactorRecipeWrapper> bioreactor = new ArrayList<>();
        BioReactorEntry.BIO_REACTOR_ENTRIES.forEach(entry -> bioreactor.add(new BioReactorRecipeWrapper(entry.getStack())));
        registry.addRecipes(bioreactor, bioReactorRecipeCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.bioReactorBlock), bioReactorRecipeCategory.getUid());

        List<ItemStackWeightedItem> item = new ArrayList<>();
        LaserDrillEntry.LASER_DRILL_ENTRIES.forEach(entry -> item.add(new ItemStackWeightedItem(entry.getStack(), entry.getWeight())));
        final int laserMaxWeight = WeightedRandom.getTotalWeight(item);
        List<LaserRecipeWrapper> laserRecipeWrappers = new ArrayList<>();
        LaserDrillEntry.LASER_DRILL_ENTRIES.forEach(entry -> laserRecipeWrappers.add(new LaserRecipeWrapper(new ItemStackWeightedItem(entry.getStack(), entry.getWeight()), laserMaxWeight, entry.getLaserMeta())));
        registry.addRecipes(laserRecipeWrappers, laserRecipeCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.laserDrillBlock), laserRecipeCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.laserBaseBlock), laserRecipeCategory.getUid());

        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.resourcefulFurnaceBlock), VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.potionEnervatorBlock), VanillaRecipeCategoryUid.BREWING);

        registry.addRecipes(Arrays.asList(
                new MachineProduceWrapper(BlockRegistry.sporesRecreatorBlock, new ItemStack(Blocks.BROWN_MUSHROOM)),
                new MachineProduceWrapper(BlockRegistry.sporesRecreatorBlock, new ItemStack(Blocks.RED_MUSHROOM)),
                new MachineProduceWrapper(BlockRegistry.sewageCompostSolidiferBlock, new ItemStack(ItemRegistry.fertilizer)),
                new MachineProduceWrapper(BlockRegistry.dyeMixerBlock, new ItemStack(Items.DYE, 1, OreDictionary.WILDCARD_VALUE)),
                new MachineProduceWrapper(BlockRegistry.lavaFabricatorBlock, new ItemStack(Items.LAVA_BUCKET)),
                new MachineProduceWrapper(BlockRegistry.waterResourcesCollectorBlock, new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE)),
                new MachineProduceWrapper(BlockRegistry.mobRelocatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.ESSENCE, 1000))),
                new MachineProduceWrapper(BlockRegistry.cropRecolectorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.SLUDGE, 1000))),
                new MachineProduceWrapper(BlockRegistry.waterCondensatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.WATER, 1000))),
                new MachineProduceWrapper(BlockRegistry.animalResourceHarvesterBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MILK, 1000))),
                new MachineProduceWrapper(BlockRegistry.mobSlaughterFactoryBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MEAT, 1000))),
                new MachineProduceWrapper(BlockRegistry.mobSlaughterFactoryBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.PINK_SLIME, 1000))),
                new MachineProduceWrapper(BlockRegistry.treeFluidExtractorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.LATEX, 1000))),
                new MachineProduceWrapper(BlockRegistry.latexProcessingUnitBlock, new ItemStack(ItemRegistry.tinyDryRubber)),
                new MachineProduceWrapper(BlockRegistry.animalByproductRecolectorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.SEWAGE, 1000))),
                new MachineProduceWrapper(BlockRegistry.lavaFabricatorBlock, FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.LAVA, 1000)))),

                machineProduceCategory.getUid());

        NonNullList<ItemStack> wrappers = NonNullList.create();
        findAllStoneWorkOutputs(wrappers, new ItemStack(Blocks.COBBLESTONE), 0);
        wrappers.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> registry.addRecipes(Collections.singletonList(new MachineProduceWrapper(BlockRegistry.materialStoneWorkFactoryBlock, stack)), machineProduceCategory.getUid()));

        List<PetrifiedBurnTimeWrapper> petrifiedBurnTimeWrappers = new ArrayList<>();
        registry.getIngredientRegistry().getFuels().stream().filter(stack -> !(stack.getItem() instanceof ItemBucket)).forEach(stack -> petrifiedBurnTimeWrappers.add(new PetrifiedBurnTimeWrapper(stack, TileEntityFurnace.getItemBurnTime(stack))));
        registry.addRecipes(petrifiedBurnTimeWrappers, petrifiedBurnTimeCategory.getUid());
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.petrifiedFuelGeneratorBlock), petrifiedBurnTimeCategory.getUid());


        //Descriptions
    }

    public ItemStack getStoneWorkOutputFrom(ItemStack stack, MaterialStoneWorkFactoryTile.Mode mode) {
        if (mode == MaterialStoneWorkFactoryTile.Mode.FURNACE)
            return FurnaceRecipes.instance().getSmeltingResult(stack).copy();
        if (mode == MaterialStoneWorkFactoryTile.Mode.CRAFT_BIG || mode == MaterialStoneWorkFactoryTile.Mode.CRAFT_SMALL) {
            return CraftingUtils.findOutput(mode == MaterialStoneWorkFactoryTile.Mode.CRAFT_BIG ? 3 : 2, stack, null);
        }
        if (mode == MaterialStoneWorkFactoryTile.Mode.GRIND) {
            return CraftingUtils.getCrushOutput(stack);
        }
        return ItemStack.EMPTY;
    }

    public void findAllStoneWorkOutputs(NonNullList<ItemStack> stacks, ItemStack last, int deep) {
        for (MaterialStoneWorkFactoryTile.Mode mode : MaterialStoneWorkFactoryTile.Mode.values()) {
            ItemStack out = getStoneWorkOutputFrom(last, mode);
            if (stacks.stream().noneMatch(stack -> stack.isItemEqual(out))) stacks.add(out);
            if (deep < 3) findAllStoneWorkOutputs(stacks, out, deep + 1);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        recipesGui = jeiRuntime.getRecipesGui();
        recipeRegistry = jeiRuntime.getRecipeRegistry();
    }
}
