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
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IRecipeProvider;
import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumRecipeProvider;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IndustrialRecipeProvider extends TitaniumRecipeProvider {

    private final NonNullLazy<List<Block>> blocks;

    public IndustrialRecipeProvider(DataGenerator generatorIn, NonNullLazy<List<Block>> blocks) {
        super(generatorIn);
        this.blocks = blocks;
    }

    @Override
    public void register(Consumer<FinishedRecipe> consumer) {
        this.blocks.get().stream().filter(block -> block instanceof BasicBlock).map(block -> (BasicBlock) block).forEach(blockBase -> blockBase.registerRecipe(consumer));
        //TRANSPORT
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> conveyorUpgradeFactory.registerRecipe(consumer));
        TransporterTypeFactory.FACTORIES.forEach(typeFactory -> typeFactory.registerRecipe(consumer));
        //TOOL
        ((IRecipeProvider) ModuleTool.MOB_IMPRISONMENT_TOOL.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.MEAT_FEEDER.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_DRILL.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_SAW.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_HAMMER.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_TRIDENT.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_BACKPACK.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_LAUNCHER.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleTool.INFINITY_NUKE.get()).registerRecipe(consumer);
        //CORE
        ((IRecipeProvider) ModuleCore.STRAW.get()).registerRecipe(consumer);
        for (RegistryObject<Item> rangeAddon : ModuleCore.RANGE_ADDONS) {
            ((IRecipeProvider) rangeAddon.get()).registerRecipe(consumer);
        }
        ((IRecipeProvider) ModuleCore.SPEED_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.SPEED_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.EFFICIENCY_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.EFFICIENCY_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.PROCESSING_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.PROCESSING_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.MACHINE_SETTINGS_COPIER.get()).registerRecipe(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.DRY_RUBBER.get()).requires(ModuleCore.TINY_DRY_RUBBER.get(), 9).save(consumer);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModuleCore.DRY_RUBBER.get()), RecipeCategory.MISC, ModuleCore.PLASTIC.get(), 0.3f, 200).unlockedBy("has_plastic", this.has(ModuleCore.DRY_RUBBER.get())).save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.PITY.get())
                .pattern("WIW").pattern("IRI").pattern("WIW")
                .define('W', ItemTags.LOGS)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.IRON_GEAR.get())
                .pattern(" P ").pattern("P P").pattern(" P ")
                .define('P', Items.IRON_INGOT)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.GOLD_GEAR.get())
                .pattern(" P ").pattern("P P").pattern(" P ")
                .define('P', Items.GOLD_INGOT)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.DIAMOND_GEAR.get())
                .pattern(" P ").pattern("P P").pattern(" P ")
                .define('P', Items.DIAMOND)
                .save(consumer);
        for (RegistryObject<Item> laserLen : ModuleCore.LASER_LENS) {
            ((IRecipeProvider) laserLen.get()).registerRecipe(consumer);
        }
        for (DyeColor value : DyeColor.values()) {
            TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.LASER_LENS[value.getId()].get())
                    .requires(Ingredient.of(Arrays.stream(ModuleCore.LASER_LENS).map(itemRegistryObject -> new ItemStack(itemRegistryObject.get())).collect(Collectors.toList()).stream()))
                    .requires(value.getTag())
                    .save(consumer, new ResourceLocation(Reference.MOD_ID, "laser_lens_" + value.getSerializedName() + "_recolor"));
        }
    }
}
