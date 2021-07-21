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

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.item.LaserLensItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.MaterialReference;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.List;
import java.util.function.Consumer;

public class IndustrialRecipeProvider extends TitaniumRecipeProvider {

    @MaterialReference(type = "gear", material = "iron")
    public static Item IRON_GEAR;
    @MaterialReference(type = "gear", material = "gold")
    public static Item GOLD_GEAR;
    @MaterialReference(type = "gear", material = "diamond")
    public static Item DIAMOND_GEAR;

    private final NonNullLazy<List<Block>> blocks;

    public IndustrialRecipeProvider(DataGenerator generatorIn, NonNullLazy<List<Block>> blocks) {
        super(generatorIn);
        this.blocks = blocks;
    }

    @Override
    public void register(Consumer<IFinishedRecipe> consumer) {
        this.blocks.get().stream().filter(block -> block instanceof BasicBlock).map(block -> (BasicBlock) block).forEach(blockBase -> blockBase.registerRecipe(consumer));
        //TRANSPORT
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.registerRecipe(consumer));
        TransporterTypeFactory.FACTORIES.forEach(typeFactory -> typeFactory.registerRecipe(consumer));
        //TOOL
        ModuleTool.MOB_IMPRISONMENT_TOOL.registerRecipe(consumer);
        ModuleTool.MEAT_FEEDER.registerRecipe(consumer);
        ModuleTool.INFINITY_DRILL.registerRecipe(consumer);
        ModuleTool.INFINITY_SAW.registerRecipe(consumer);
        ModuleTool.INFINITY_HAMMER.registerRecipe(consumer);
        ModuleTool.INFINITY_TRIDENT.registerRecipe(consumer);
        ModuleTool.INFINITY_BACKPACK.registerRecipe(consumer);
        ModuleTool.INFINITY_LAUNCHER.registerRecipe(consumer);
        ModuleTool.INFINITY_NUKE.registerRecipe(consumer);
        //CORE
        ModuleCore.STRAW.registerRecipe(consumer);
        for (RangeAddonItem rangeAddon : ModuleCore.RANGE_ADDONS) {
            rangeAddon.registerRecipe(consumer);
        }
        ModuleCore.SPEED_ADDON_1.registerRecipe(consumer);
        ModuleCore.SPEED_ADDON_2.registerRecipe(consumer);
        ModuleCore.EFFICIENCY_ADDON_1.registerRecipe(consumer);
        ModuleCore.EFFICIENCY_ADDON_2.registerRecipe(consumer);
        ModuleCore.PROCESSING_ADDON_1.registerRecipe(consumer);
        ModuleCore.PROCESSING_ADDON_2.registerRecipe(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.DRY_RUBBER).addIngredient(ModuleCore.TINY_DRY_RUBBER, 9).build(consumer);
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ModuleCore.DRY_RUBBER), ModuleCore.PLASTIC, 0.3f, 200).addCriterion("has_plastic", this.hasItem(ModuleCore.DRY_RUBBER)).build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.PITY)
                .patternLine("WIW").patternLine("IRI").patternLine("WIW")
                .key('W', ItemTags.LOGS)
                .key('I', Tags.Items.INGOTS_IRON)
                .key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(IRON_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.IRON_INGOT)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(GOLD_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.GOLD_INGOT)
                .build(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DIAMOND_GEAR)
                .patternLine(" P ").patternLine("P P").patternLine(" P ")
                .key('P', Items.DIAMOND)
                .build(consumer);
        for (LaserLensItem laserLen : ModuleCore.LASER_LENS) {
            laserLen.registerRecipe(consumer);
        }
        for (DyeColor value : DyeColor.values()) {
            TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.LASER_LENS[value.getId()])
                    .addIngredient(Ingredient.fromItems(ModuleCore.LASER_LENS))
                    .addIngredient(value.getTag())
                    .build(consumer, new ResourceLocation(Reference.MOD_ID, "laser_lens_"+value.getString()+ "_recolor"));
        }
    }
}
