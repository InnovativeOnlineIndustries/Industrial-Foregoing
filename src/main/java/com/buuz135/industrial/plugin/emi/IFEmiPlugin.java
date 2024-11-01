package com.buuz135.industrial.plugin.emi;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.block.generator.MycelialGeneratorBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.plugin.RecipeViewerHelper;
import com.buuz135.industrial.plugin.emi.category.*;
import com.buuz135.industrial.plugin.emi.recipe.*;
import com.buuz135.industrial.plugin.jei.category.BioReactorRecipeCategory;
import com.buuz135.industrial.plugin.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.utils.IFAttachments;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.util.TagUtil;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@EmiEntrypoint
public class IFEmiPlugin implements EmiPlugin {

    public static final DissolutionChamberEmiCategory DISSOLUTION_CHAMBER_EMI_CATEGORY = new DissolutionChamberEmiCategory();
    public static final BioreactorEmiCategory BIOREACTOR_EMI_CATEGORY = new BioreactorEmiCategory();
    public static final FermentationStationEmiCategory FERMENTATION_STATION_EMI_CATEGORY = new FermentationStationEmiCategory();
    public static final FluidExtractorEmiCategory FLUID_EXTRACTOR_EMI_CATEGORY = new FluidExtractorEmiCategory();
    public static final FluidSieveEmiCategory FLUID_SIEVE_EMI_CATEGORY = new FluidSieveEmiCategory();
    public static final LaserDrillOreEmiCategory LASER_DRILL_ORE_EMI_CATEGORY = new LaserDrillOreEmiCategory();
    public static final LaserDrillFluidEmiCategory LASER_DRILL_FLUID_EMI_CATEGORY = new LaserDrillFluidEmiCategory();
    public static final OreWasherEmiCategory ORE_WASHER_EMI_CATEGORY = new OreWasherEmiCategory();
    public static final StoneWorkEmiCategory STONE_WORK_EMI_CATEGORY = new StoneWorkEmiCategory();
    public static final StoneWorkGeneratorEmiCategory STONE_WORK_GENERATOR = new StoneWorkGeneratorEmiCategory();

    @Override
    public void initialize(EmiInitRegistry registry) {

    }

    @Override
    public void register(EmiRegistry registry) {
        RecipeManager manager = registry.getRecipeManager();
        //DISSOLUTION CHAMBER
        registry.addCategory(DISSOLUTION_CHAMBER_EMI_CATEGORY);
        registry.addWorkstation(DISSOLUTION_CHAMBER_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleCore.DISSOLUTION_CHAMBER.getBlock())));
        for (RecipeHolder<?> recipe : manager.getAllRecipesFor((RecipeType<DissolutionChamberRecipe>) ModuleCore.DISSOLUTION_TYPE.get())) {
            registry.addRecipe(new DissChamberEmiRecipe((RecipeHolder<DissolutionChamberRecipe>) recipe));
        }
        //BIOREACTOR
        registry.addCategory(BIOREACTOR_EMI_CATEGORY);
        registry.addWorkstation(BIOREACTOR_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleGenerator.BIOREACTOR.getBlock())));
        for (BioReactorRecipeCategory.ReactorRecipeWrapper generateBioreactorRecipe : RecipeViewerHelper.generateBioreactorRecipes()) {
            registry.addRecipe(new BioreactorEmiRecipe(generateBioreactorRecipe.getStack(), generateBioreactorRecipe.getFluid()));
        }

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
        //WASHING
        registry.addCategory(ORE_WASHER_EMI_CATEGORY);
        registry.addWorkstation(ORE_WASHER_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.WASHING_FACTORY.getBlock())));
        washer.forEach(wash -> registry.addRecipe(new OreWasherEmiRecipe(wash)));
        //FERMENTATION
        registry.addCategory(FERMENTATION_STATION_EMI_CATEGORY);
        registry.addWorkstation(FERMENTATION_STATION_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.FERMENTATION_STATION.getBlock())));
        fluidEntryFermenters.forEach(oreFluidEntryFermenter -> registry.addRecipe(new FermentationStationEmiRecipe(oreFluidEntryFermenter)));
        //SIEVE
        registry.addCategory(FLUID_SIEVE_EMI_CATEGORY);
        registry.addWorkstation(FLUID_SIEVE_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.FLUID_SIEVING_MACHINE.getBlock())));
        fluidSieve.forEach(fluidSieves -> registry.addRecipe(new FluidSieveEmiRecipe(fluidSieves)));

        //FLUID_EXTRACTOR
        registry.addCategory(FLUID_EXTRACTOR_EMI_CATEGORY);
        registry.addWorkstation(FLUID_EXTRACTOR_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleCore.FLUID_EXTRACTOR.getBlock())));
        for (RecipeHolder<?> recipe : manager.getAllRecipesFor((RecipeType<FluidExtractorRecipe>) ModuleCore.FLUID_EXTRACTOR_TYPE.get())) {
            registry.addRecipe(new FluidExtractorEmiRecipe((RecipeHolder<FluidExtractorRecipe>) recipe));
        }

        //LASER DRILL ORE
        registry.addCategory(LASER_DRILL_ORE_EMI_CATEGORY);
        registry.addWorkstation(LASER_DRILL_ORE_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.ORE_LASER_BASE.getBlock())));
        for (RecipeHolder<?> recipe : manager.getAllRecipesFor((RecipeType<LaserDrillOreRecipe>) ModuleCore.LASER_DRILL_TYPE.get())) {
            registry.addRecipe(new LaserDrillOreEmiRecipe((RecipeHolder<LaserDrillOreRecipe>) recipe));
        }
        //LASER DRILL FLUID
        registry.addCategory(LASER_DRILL_FLUID_EMI_CATEGORY);
        registry.addWorkstation(LASER_DRILL_FLUID_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.FLUID_LASER_BASE.getBlock())));
        for (RecipeHolder<?> recipe : manager.getAllRecipesFor((RecipeType<LaserDrillFluidRecipe>) ModuleCore.LASER_DRILL_FLUID_TYPE.get())) {
            registry.addRecipe(new LaserDrillFluidEmiRecipe((RecipeHolder<LaserDrillFluidRecipe>) recipe));
        }

        //STONEWORK
        registry.addCategory(STONE_WORK_EMI_CATEGORY);
        registry.addWorkstation(STONE_WORK_EMI_CATEGORY, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.getBlock())));
        RecipeViewerHelper.getStoneWork().forEach(stoneWorkWrapper -> registry.addRecipe(new StoneWorkEmiRecipe(stoneWorkWrapper)));

        //STONEWORK GENERATOR
        registry.addCategory(STONE_WORK_GENERATOR);
        registry.addWorkstation(STONE_WORK_GENERATOR, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.MATERIAL_STONEWORK_FACTORY.getBlock())));
        for (RecipeHolder<?> recipe : manager.getAllRecipesFor((RecipeType<StoneWorkGenerateRecipe>) ModuleCore.STONEWORK_GENERATE_TYPE.get())) {
            registry.addRecipe(new StoneWorkGeneratorEmiRecipe((RecipeHolder<StoneWorkGenerateRecipe>) recipe));
        }

        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, EmiIngredient.of(Ingredient.of(ModuleResourceProduction.RESOURCEFUL_FURNACE.getBlock())));

        for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
            ItemLike display = type.getDisplay();
            for (BlockWithTile mycelialGenerator : ModuleGenerator.MYCELIAL_GENERATORS) {
                if (((MycelialGeneratorBlock) mycelialGenerator.getBlock()).getType().equals(type)) {
                    display = mycelialGenerator.getBlock();
                }
            }
            MycelialGeneratorEmiCategory category = new MycelialGeneratorEmiCategory(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mycellial_" + type.getName()), display.asItem(), type);
            registry.addCategory(category);
            registry.addWorkstation(category, EmiStack.of(display));
            var recipes = type.getRecipes(IFAttachments.registryAccess()).stream().sorted(Comparator.comparingInt(value -> ((MycelialGeneratorRecipe) value).getTicks() * ((MycelialGeneratorRecipe) value).getPowerTick()).reversed()).collect(Collectors.toList());
            recipes.forEach(recipe -> {
                registry.addRecipe(new MycelialGeneratorEmiRecipe(type, recipe, category));
            });

        }

    }
}
