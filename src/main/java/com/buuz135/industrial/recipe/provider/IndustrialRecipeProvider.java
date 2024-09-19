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
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapelessRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class IndustrialRecipeProvider extends RecipeProvider {

    private final Lazy<List<Block>> blocks;

    public IndustrialRecipeProvider(DataGenerator generator, Lazy<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> prov) {
        super(generator.getPackOutput(), prov);
        this.blocks = blocksToProcess;
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        this.blocks.get().stream().filter(block -> block instanceof BasicBlock).map(block -> (BasicBlock) block).forEach(blockBase -> blockBase.registerRecipe(consumer));
        TitaniumShapedRecipeBuilder.shapedRecipe(ModuleCore.PINK_SLIME_BLOCK.get())
                .pattern("PPP").pattern("PPP").pattern("PPP")
                .define('P', ModuleCore.PINK_SLIME_ITEM.get())
                .save(consumer);
        TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.PINK_SLIME_ITEM.get(), 9)
                .requires(ModuleCore.PINK_SLIME_BLOCK.get())
                .save(consumer);
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
        for (DeferredHolder<Item, Item> rangeAddon : ModuleCore.RANGE_ADDONS) {
            ((IRecipeProvider) rangeAddon.get()).registerRecipe(consumer);
        }
        ((IRecipeProvider) ModuleCore.SPEED_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.SPEED_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.EFFICIENCY_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.EFFICIENCY_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.PROCESSING_ADDON_1.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.PROCESSING_ADDON_2.get()).registerRecipe(consumer);
        ((IRecipeProvider) ModuleCore.MACHINE_SETTINGS_COPIER.get()).registerRecipe(consumer);
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
        for (DeferredHolder<Item, Item> laserLen : ModuleCore.LASER_LENS) {
            ((IRecipeProvider) laserLen.get()).registerRecipe(consumer);
        }
        for (DyeColor value : DyeColor.values()) {
            TitaniumShapelessRecipeBuilder.shapelessRecipe(ModuleCore.LASER_LENS[value.getId()].get())
                    .requires(Ingredient.of(Arrays.stream(ModuleCore.LASER_LENS).map(itemRegistryObject -> new ItemStack(itemRegistryObject.get())).collect(Collectors.toList()).stream()))
                    .requires(value.getTag())
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "laser_lens_" + value.getSerializedName() + "_recolor"));
        }
        IndustrialSerializableProvider.init(consumer);
    }
}
