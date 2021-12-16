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

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.block.MachineFrameBlock;
import com.buuz135.industrial.block.core.DarkGlassBlock;
import com.buuz135.industrial.block.core.DissolutionChamberBlock;
import com.buuz135.industrial.block.core.FluidExtractorBlock;
import com.buuz135.industrial.block.core.LatexProcessingUnitBlock;
import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.fluid.OreFluidInstance;
import com.buuz135.industrial.item.FertilizerItem;
import com.buuz135.industrial.item.ItemStraw;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.item.RecipelessCustomItem;
import com.buuz135.industrial.item.addon.EfficiencyAddonItem;
import com.buuz135.industrial.item.addon.ProcessingAddonItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.item.addon.SpeedAddonItem;
import com.buuz135.industrial.proxy.StrawRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.fluid.TitaniumFluidInstance;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;

public class ModuleCore implements IModule {

    public static Rarity PITY_RARITY;
    public static Rarity SIMPLE_RARITY;
    public static Rarity ADVANCED_RARITY;
    public static Rarity SUPREME_RARITY;

    public static AdvancedTitaniumTab TAB_CORE = new AdvancedTitaniumTab(Reference.MOD_ID + "_core", true);

    public static RegistryObject<Item> TINY_DRY_RUBBER;
    public static RegistryObject<Item> DRY_RUBBER;
    public static RegistryObject<Item> PLASTIC;
    public static RegistryObject<Item> FERTILIZER;
    public static RegistryObject<Item> PINK_SLIME_ITEM;
    public static RegistryObject<Item> PINK_SLIME_INGOT;
    public static RegistryObject<Item> STRAW;
    public static RegistryObject<Block> PITY;
    public static RegistryObject<Block> SIMPLE;
    public static RegistryObject<Block> ADVANCED;
    public static RegistryObject<Block> SUPREME;
    public static RegistryObject<Block> FLUID_EXTRACTOR;
    public static RegistryObject<Block> LATEX_PROCESSING;
    public static RegistryObject<Block> DISSOLUTION_CHAMBER;
    public static RegistryObject<Item>[] RANGE_ADDONS = new RegistryObject[12];
    public static RegistryObject<Item>[] LASER_LENS = new RegistryObject[DyeColor.values().length];
    public static RegistryObject<Item> SPEED_ADDON_1;
    public static RegistryObject<Item> SPEED_ADDON_2;
    public static RegistryObject<Item> EFFICIENCY_ADDON_1;
    public static RegistryObject<Item> EFFICIENCY_ADDON_2;
    public static RegistryObject<Item> PROCESSING_ADDON_1;
    public static RegistryObject<Item> PROCESSING_ADDON_2;
    public static RegistryObject<Block> DARK_GLASS;

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


    @Override
    public void generateFeatures(DeferredRegistryHelper helper) {
        PITY_RARITY = Rarity.create("pity", ChatFormatting.GREEN);
        SIMPLE_RARITY = Rarity.create("simple", ChatFormatting.AQUA);
        ADVANCED_RARITY = Rarity.create("advanced", ChatFormatting.LIGHT_PURPLE);
        SUPREME_RARITY = Rarity.create("supreme", ChatFormatting.GOLD);
        PITY = helper.register(Block.class, "machine_frame_pity", () ->  new MachineFrameBlock(PITY_RARITY, TAB_CORE));
        SIMPLE = helper.register(Block.class, "machine_frame_simple", () ->  new MachineFrameBlock(SIMPLE_RARITY, TAB_CORE));
        ADVANCED = helper.register(Block.class, "machine_frame_advanced", () ->  new MachineFrameBlock(ADVANCED_RARITY, TAB_CORE));
        SUPREME = helper.register(Block.class, "machine_frame_supreme", () ->  new MachineFrameBlock(SUPREME_RARITY, TAB_CORE));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::onClient);
        EventManager.forge(TickEvent.WorldTickEvent.class).
                filter(worldTickEvent -> worldTickEvent.phase == TickEvent.Phase.END && worldTickEvent.type == TickEvent.Type.WORLD && worldTickEvent.world.getGameTime() % 40 == 0 && FluidExtractorTile.EXTRACTION.containsKey(worldTickEvent.world.dimensionType())).
                process(worldTickEvent -> FluidExtractorTile.EXTRACTION.get(worldTickEvent.world.dimensionType()).values().forEach(blockPosFluidExtractionProgressHashMap -> blockPosFluidExtractionProgressHashMap.keySet().forEach(pos -> worldTickEvent.world.destroyBlockProgress(blockPosFluidExtractionProgressHashMap.get(pos).getBreakID(), pos, blockPosFluidExtractionProgressHashMap.get(pos).getProgress())))).subscribe();
        EventManager.mod(RegistryEvent.NewRegistry.class).process(register -> IFRegistries.poke()).subscribe();
        EventManager.modGeneric(RegistryEvent.Register.class, StrawHandler.class).process(register -> StrawRegistry.register((RegistryEvent.Register<StrawHandler>) register)).subscribe();
        for (int i = 0; i < RANGE_ADDONS.length; i++) {
            int finalI = i;
            RANGE_ADDONS[i] = helper.register(Item.class, "range_addon" + i, () -> new RangeAddonItem(finalI, TAB_CORE));
        }
        TAB_CORE.addIconStack(() -> new ItemStack(PLASTIC.orElse(Items.STONE)));
        for (DyeColor value : DyeColor.values()) {
            LASER_LENS[value.getId()] = helper.register(Item.class, "laser_lens" + value.getId(), () -> new LaserLensItem(value.getId()));
        }
        TINY_DRY_RUBBER = helper.register(Item.class, "tinydryrubber", () -> new RecipelessCustomItem("tinydryrubber",TAB_CORE));
        DRY_RUBBER = helper.register(Item.class, "dryrubber", () -> new RecipelessCustomItem("dryrubber", TAB_CORE));
        PLASTIC = helper.register(Item.class, "plastic", () -> new RecipelessCustomItem("plastic", TAB_CORE));
        FERTILIZER = helper.register(Item.class, "fertilizer", () -> new FertilizerItem(TAB_CORE));
        PINK_SLIME_ITEM = helper.register(Item.class, "pink_slime", () -> new RecipelessCustomItem("pink_slime", TAB_CORE));
        PINK_SLIME_INGOT = helper.register(Item.class, "pink_slime_ingot", () -> new RecipelessCustomItem("pink_slime_ingot", TAB_CORE));
        STRAW = helper.register(Item.class, "straw", () -> new ItemStraw(TAB_CORE));
        FLUID_EXTRACTOR = helper.register(Block.class, "fluid_extractor", FluidExtractorBlock::new);
        LATEX_PROCESSING = helper.register(Block.class, "latex_processing_unit", LatexProcessingUnitBlock::new);
        DISSOLUTION_CHAMBER = helper.register(Block.class, "dissolution_chamber", DissolutionChamberBlock::new);
        SPEED_ADDON_1 = helper.register(Item.class, "speed_addon_1", () -> new SpeedAddonItem(1, TAB_CORE));
        SPEED_ADDON_2 = helper.register(Item.class, "speed_addon_2", () -> new SpeedAddonItem(2, TAB_CORE));
        EFFICIENCY_ADDON_1 = helper.register(Item.class, "efficiency_addon_1", () -> new EfficiencyAddonItem(1, TAB_CORE));
        EFFICIENCY_ADDON_2 = helper.register(Item.class, "efficiency_addon_2", () -> new EfficiencyAddonItem(2, TAB_CORE));
        PROCESSING_ADDON_1 = helper.register(Item.class, "processing_addon_1", () -> new ProcessingAddonItem(1, TAB_CORE));
        PROCESSING_ADDON_2 = helper.register(Item.class, "processing_addon_2", () -> new ProcessingAddonItem(2, TAB_CORE));
        DARK_GLASS = helper.register(Block.class, "dark_glass", DarkGlassBlock::new);

        LATEX = new TitaniumFluidInstance(Reference.MOD_ID, "latex", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_flow")), true, TAB_CORE);
        MEAT =  new TitaniumFluidInstance(Reference.MOD_ID, "meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_flow")), true, TAB_CORE);
        SEWAGE = new TitaniumFluidInstance(Reference.MOD_ID, "sewage", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_flow")), true, TAB_CORE);
        ESSENCE =  new TitaniumFluidInstance(Reference.MOD_ID, "essence", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/essence_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/essence_flow")), true, TAB_CORE);
        SLUDGE =  new TitaniumFluidInstance(Reference.MOD_ID, "sludge", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sludge_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sludge_flow")), true, TAB_CORE);
        PINK_SLIME = new TitaniumFluidInstance(Reference.MOD_ID, "pink_slime", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/pink_slime_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/pink_slime_flow")), true, TAB_CORE);
        BIOFUEL = new TitaniumFluidInstance(Reference.MOD_ID, "biofuel", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/biofuel_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/biofuel_flow")), true, TAB_CORE);
        ETHER =  new TitaniumFluidInstance(Reference.MOD_ID, "ether_gas", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_flow")).gaseous(), true, TAB_CORE);
        RAW_ORE_MEAT = new OreFluidInstance(Reference.MOD_ID, "raw_ore_meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/raw_ore_meat_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/raw_ore_meat_flow")), true, TAB_CORE);
        FERMENTED_ORE_MEAT =new OreFluidInstance(Reference.MOD_ID, "fermented_ore_meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/ether_gas_flow")), true, TAB_CORE);
        LATEX.addAlternatives(helper);
        MEAT.addAlternatives(helper);
        SEWAGE.addAlternatives(helper);
        ESSENCE.addAlternatives(helper);
        SLUDGE.addAlternatives(helper);
        PINK_SLIME.addAlternatives(helper);
        BIOFUEL.addAlternatives(helper);
        ETHER.addAlternatives(helper);
        RAW_ORE_MEAT.addAlternatives(helper);
        FERMENTED_ORE_MEAT.addAlternatives(helper);
    }

    @OnlyIn(Dist.CLIENT)
    public void textureStitch(TextureStitchEvent.Pre event) {
        //event.addSprite(LATEX.getSourceFluid().getAttributes().getFlowingTexture()); ??
        //event.addSprite(LATEX.getSourceFluid().getAttributes().getStillTexture());
    }
    @OnlyIn(Dist.CLIENT)
    public void onClient(){
        EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch).subscribe();
    }
}
