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

package com.buuz135.industrial.module;


import com.buuz135.industrial.block.IndustrialBlockItem;
import com.buuz135.industrial.block.MachineFrameBlock;
import com.buuz135.industrial.block.core.DissolutionChamberBlock;
import com.buuz135.industrial.block.core.FluidExtractorBlock;
import com.buuz135.industrial.block.core.LatexProcessingUnitBlock;
import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.fluid.OreFluidInstance;
import com.buuz135.industrial.fluid.OreTitaniumFluidType;
import com.buuz135.industrial.item.*;
import com.buuz135.industrial.item.addon.EfficiencyAddonItem;
import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.item.addon.SpeedAddonItem;
import com.buuz135.industrial.recipe.*;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.CustomRarity;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.straw.*;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.fluid.ClientFluidTypeExtensions;
import com.hrznstudio.titanium.fluid.TitaniumFluidInstance;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.recipe.serializer.CodecRecipeSerializer;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;


public class ModuleCore implements IModule {

    public static TitaniumTab TAB_CORE = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "core"));

    public static DeferredHolder<Item, Item> DRY_RUBBER;
    public static DeferredHolder<Item, Item> PLASTIC;
    public static DeferredHolder<Item, Item> FERTILIZER;
    public static DeferredHolder<Item, Item> PINK_SLIME_ITEM;
    public static DeferredHolder<Item, Item> PINK_SLIME_INGOT;
    public static DeferredHolder<Item, Item> STRAW;
    public static DeferredHolder<Block, Block> PITY;
    public static DeferredHolder<Block, Block> SIMPLE;
    public static DeferredHolder<Block, Block> ADVANCED;
    public static DeferredHolder<Block, Block> SUPREME;
    public static BlockWithTile FLUID_EXTRACTOR;
    public static BlockWithTile LATEX_PROCESSING;
    public static BlockWithTile DISSOLUTION_CHAMBER;
    public static DeferredHolder<Item, Item>[] RANGE_ADDONS = new DeferredHolder[12];
    public static DeferredHolder<Item, Item>[] LASER_LENS = new DeferredHolder[DyeColor.values().length];
    public static DeferredHolder<Item, Item> SPEED_ADDON_1;
    public static DeferredHolder<Item, Item> SPEED_ADDON_2;
    public static DeferredHolder<Item, Item> EFFICIENCY_ADDON_1;
    public static DeferredHolder<Item, Item> EFFICIENCY_ADDON_2;
    public static DeferredHolder<Item, Item> PROCESSING_ADDON_1;
    public static DeferredHolder<Item, Item> PROCESSING_ADDON_2;
    public static DeferredHolder<Item, Item> MACHINE_SETTINGS_COPIER;

    public static TitaniumFluidInstance LATEX;
    public static TitaniumFluidInstance MEAT;
    public static TitaniumFluidInstance SEWAGE;
    public static TitaniumFluidInstance ESSENCE;
    public static TitaniumFluidInstance SLUDGE;
    public static TitaniumFluidInstance PINK_SLIME;
    public static TitaniumFluidInstance BIOFUEL;
    public static TitaniumFluidInstance ETHER;
    public static OreFluidInstance RAW_ORE_MEAT;
    public static OreFluidInstance FERMENTED_ORE_MEAT;

    public static DeferredHolder<Item, Item> IRON_GEAR;
    public static DeferredHolder<Item, Item> GOLD_GEAR;
    public static DeferredHolder<Item, Item> DIAMOND_GEAR;

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> DISSOLUTION_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> DISSOLUTION_TYPE;
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> FLUID_EXTRACTOR_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> FLUID_EXTRACTOR_TYPE;
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> LASER_DRILL_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> LASER_DRILL_TYPE;
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> LASER_DRILL_FLUID_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> LASER_DRILL_FLUID_TYPE;
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> STONEWORK_GENERATE_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> STONEWORK_GENERATE_TYPE;
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CRUSHER_SERIALIZER;
    public static DeferredHolder<RecipeType<?>, RecipeType<?>> CRUSHER_TYPE;


    @Override
    public void generateFeatures(DeferredRegistryHelper helper) {

        PITY = helper.registerBlockWithItem("machine_frame_pity", () -> new MachineFrameBlock(CustomRarity.PITY.getValue(), TAB_CORE), (block) -> () -> new MachineFrameBlock.MachineFrameItem(block.get(), CustomRarity.PITY.getValue(), TAB_CORE), TAB_CORE);
        SIMPLE = helper.registerBlockWithItem("machine_frame_simple", () -> new MachineFrameBlock(CustomRarity.SIMPLE.getValue(), TAB_CORE), (block) -> () -> new MachineFrameBlock.MachineFrameItem(block.get(), CustomRarity.SIMPLE.getValue(), TAB_CORE), TAB_CORE);
        ADVANCED = helper.registerBlockWithItem("machine_frame_advanced", () -> new MachineFrameBlock(CustomRarity.ADVANCED.getValue(), TAB_CORE), (block) -> () -> new MachineFrameBlock.MachineFrameItem(block.get(), CustomRarity.ADVANCED.getValue(), TAB_CORE), TAB_CORE);
        SUPREME = helper.registerBlockWithItem("machine_frame_supreme", () -> new MachineFrameBlock(CustomRarity.SUPREME.getValue(), TAB_CORE), (block) -> () -> new MachineFrameBlock.MachineFrameItem(block.get(), CustomRarity.SUPREME.getValue(), TAB_CORE), TAB_CORE);
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onClient);
        EventManager.forge(LevelTickEvent.Post.class).
                filter(worldTickEvent -> worldTickEvent.getLevel().getGameTime() % 40 == 0 && FluidExtractorTile.EXTRACTION.containsKey(worldTickEvent.getLevel().dimensionType())).
                process(worldTickEvent -> FluidExtractorTile.EXTRACTION.get(worldTickEvent.getLevel().dimensionType()).values().forEach(blockPosFluidExtractionProgressHashMap -> blockPosFluidExtractionProgressHashMap.keySet().forEach(pos -> worldTickEvent.getLevel().destroyBlockProgress(blockPosFluidExtractionProgressHashMap.get(pos).getBreakID(), pos, blockPosFluidExtractionProgressHashMap.get(pos).getProgress())))).subscribe();
        for (int i = 0; i < RANGE_ADDONS.length; i++) {
            int finalI = i;
            RANGE_ADDONS[i] = helper.registerGeneric(Registries.ITEM, "range_addon_tier_" + i, () -> new RangeAddonItem(finalI, TAB_CORE));
        }
        for (DyeColor value : DyeColor.values()) {
            LASER_LENS[value.getId()] = helper.registerGeneric(Registries.ITEM, value.getName() + "_laser_lens", () -> new LaserLensItem(value));
        }
        DRY_RUBBER = helper.registerGeneric(Registries.ITEM, "dryrubber", () -> new RecipelessCustomItem("dryrubber", TAB_CORE));
        PLASTIC = helper.registerGeneric(Registries.ITEM, "plastic", () -> new RecipelessCustomItem("plastic", TAB_CORE));
        FERTILIZER = helper.registerGeneric(Registries.ITEM, "fertilizer", () -> new FertilizerItem(TAB_CORE));
        PINK_SLIME_ITEM = helper.registerGeneric(Registries.ITEM, "pink_slime", () -> new RecipelessCustomItem("pink_slime", TAB_CORE));
        PINK_SLIME_INGOT = helper.registerGeneric(Registries.ITEM, "pink_slime_ingot", () -> new RecipelessCustomItem("pink_slime_ingot", TAB_CORE));
        STRAW = helper.registerGeneric(Registries.ITEM, "straw", () -> new ItemStraw(TAB_CORE));
        FLUID_EXTRACTOR = helper.registerBlockWithTileItem("fluid_extractor", FluidExtractorBlock::new, blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_CORE), TAB_CORE);
        LATEX_PROCESSING = helper.registerBlockWithTileItem("latex_processing_unit", LatexProcessingUnitBlock::new, blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_CORE), TAB_CORE);
        DISSOLUTION_CHAMBER = helper.registerBlockWithTileItem("dissolution_chamber", DissolutionChamberBlock::new, blockRegistryObject -> () -> new IndustrialBlockItem(blockRegistryObject.get(), TAB_CORE), TAB_CORE);
        SPEED_ADDON_1 = helper.registerGeneric(Registries.ITEM, "speed_addon_tier_1", () -> new SpeedAddonItem(1, TAB_CORE));
        SPEED_ADDON_2 = helper.registerGeneric(Registries.ITEM, "speed_addon_tier_2", () -> new SpeedAddonItem(2, TAB_CORE));
        EFFICIENCY_ADDON_1 = helper.registerGeneric(Registries.ITEM, "efficiency_addon_tier_1", () -> new EfficiencyAddonItem(1, TAB_CORE));
        EFFICIENCY_ADDON_2 = helper.registerGeneric(Registries.ITEM, "efficiency_addon_tier_2", () -> new EfficiencyAddonItem(2, TAB_CORE));
        PROCESSING_ADDON_1 = helper.registerGeneric(Registries.ITEM, "processing_addon_tier_1", () -> new ProcessingAddonItem(1, TAB_CORE));
        PROCESSING_ADDON_2 = helper.registerGeneric(Registries.ITEM, "processing_addon_tier_2", () -> new ProcessingAddonItem(2, TAB_CORE));
        MACHINE_SETTINGS_COPIER = helper.registerGeneric(Registries.ITEM, "machine_settings_copier", () -> new MachineSettingCopier(TAB_CORE));

        LATEX = new TitaniumFluidInstance(helper, "latex", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/latex_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/latex_flow")), TAB_CORE);
        MEAT = new TitaniumFluidInstance(helper, "meat", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/meat_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/meat_flow")), TAB_CORE);
        SEWAGE = new TitaniumFluidInstance(helper, "sewage", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/sewage_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/sewage_flow")), TAB_CORE);
        ESSENCE = new TitaniumFluidInstance(helper, "essence", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/essence_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/essence_flow")), TAB_CORE);
        SLUDGE = new TitaniumFluidInstance(helper, "sludge", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/sludge_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/sludge_flow")), TAB_CORE);
        PINK_SLIME = new TitaniumFluidInstance(helper, "pink_slime", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/pink_slime_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/pink_slime_flow")), TAB_CORE);
        BIOFUEL = new TitaniumFluidInstance(helper, "biofuel", FluidType.Properties.create().density(1000), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/biofuel_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/biofuel_flow")), TAB_CORE);
        ETHER = new TitaniumFluidInstance(helper, "ether_gas", FluidType.Properties.create().density(0), new ClientFluidTypeExtensions(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/ether_gas_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/ether_gas_flow")), TAB_CORE);
        RAW_ORE_MEAT = new OreFluidInstance(helper, "raw_ore_meat", FluidType.Properties.create().density(1000), new OreTitaniumFluidType.Client(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/raw_ore_meat_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/raw_ore_meat_flow")), TAB_CORE);
        FERMENTED_ORE_MEAT = new OreFluidInstance(helper, "fermented_ore_meat", FluidType.Properties.create().density(1000), new OreTitaniumFluidType.Client(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/ether_gas_still"), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "block/fluids/ether_gas_flow")), TAB_CORE);

        IRON_GEAR = helper.registerGeneric(Registries.ITEM, "iron_gear", () -> new RecipelessCustomItem("iron_gear", TAB_CORE));
        GOLD_GEAR = helper.registerGeneric(Registries.ITEM, "gold_gear", () -> new RecipelessCustomItem("gold_gear", TAB_CORE));
        DIAMOND_GEAR = helper.registerGeneric(Registries.ITEM, "diamond_gear", () -> new RecipelessCustomItem("diamond", TAB_CORE));

        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "water", WaterStrawHandler::new);
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "lava", LavaStrawHandler::new);
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "milk", MilkStrawHandler::new);
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "essence", EssenceStrawHandler::new);
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "biofuel", () -> new PotionStrawHandler(ModuleCore.BIOFUEL.getSourceFluid())
                .addPotion(MobEffects.MOVEMENT_SPEED, 800, 0)
                .addPotion(MobEffects.DIG_SPEED, 800, 0));
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "sludge", () -> new PotionStrawHandler(ModuleCore.SLUDGE.getSourceFluid())
                .addPotion(MobEffects.WITHER, 600, 0)
                .addPotion(MobEffects.BLINDNESS, 1000, 0)
                .addPotion(MobEffects.MOVEMENT_SLOWDOWN, 1200, 1));
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "sewage",  () -> new PotionStrawHandler(ModuleCore.SEWAGE.getSourceFluid())
                .addPotion(MobEffects.CONFUSION, 1200, 0)
                .addPotion(MobEffects.MOVEMENT_SLOWDOWN, 1200, 0));
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "meat", () -> new PotionStrawHandler(ModuleCore.MEAT.getSourceFluid())
                .addPotion(MobEffects.ABSORPTION, 100, 2)
                .addPotion(MobEffects.SATURATION, 300, 2));
        helper.registerGeneric(IFRegistries.STRAW_HANDLER_REGISTRY_KEY, "latex",  () -> new PotionStrawHandler(ModuleCore.LATEX.getSourceFluid())
                .addPotion(MobEffects.POISON, 1000, 2)
                .addPotion(MobEffects.MOVEMENT_SLOWDOWN, 1000, 2));

        DISSOLUTION_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "dissolution_chamber", () -> new CodecRecipeSerializer<>(DissolutionChamberRecipe.class, DISSOLUTION_TYPE, DissolutionChamberRecipe.CODEC));
        DISSOLUTION_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "dissolution_chamber", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "dissolution_chamber")));
        FLUID_EXTRACTOR_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "fluid_extractor", () -> new CodecRecipeSerializer<>(FluidExtractorRecipe.class, FLUID_EXTRACTOR_TYPE, FluidExtractorRecipe.CODEC));
        FLUID_EXTRACTOR_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "fluid_extractor", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_extractor")));
        LASER_DRILL_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "laser_drill_ore", () -> new CodecRecipeSerializer<>(LaserDrillOreRecipe.class, LASER_DRILL_TYPE, LaserDrillOreRecipe.CODEC));
        LASER_DRILL_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "laser_drill_ore", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "laser_drill_ore")));
        LASER_DRILL_FLUID_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "laser_drill_fluid", () -> new CodecRecipeSerializer<>(LaserDrillFluidRecipe.class, LASER_DRILL_FLUID_TYPE, LaserDrillFluidRecipe.CODEC));
        LASER_DRILL_FLUID_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "laser_drill_fluid", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "laser_drill_fluid")));
        STONEWORK_GENERATE_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "stonework_generate", () -> new CodecRecipeSerializer<>(StoneWorkGenerateRecipe.class, STONEWORK_GENERATE_TYPE, StoneWorkGenerateRecipe.CODEC));
        STONEWORK_GENERATE_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "stonework_generate", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "stonework_generate")));
        CRUSHER_SERIALIZER = helper.registerGeneric(Registries.RECIPE_SERIALIZER, "crusher", () -> new CodecRecipeSerializer<>(CrusherRecipe.class, CRUSHER_TYPE, CrusherRecipe.CODEC));
        CRUSHER_TYPE = helper.registerGeneric(Registries.RECIPE_TYPE, "crusher", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "crusher")));
    }

}
