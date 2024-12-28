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

import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class IndustrialTagsProvider {

    public static class Blocks extends BlockTagsProvider {

        public Blocks(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(dataGenerator.getPackOutput(), lookupProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider p_256380_) {
            tag(IndustrialTags.Blocks.MACHINE_FRAME_PITY).add(ModuleCore.PITY.get());
            tag(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE).add(ModuleCore.SIMPLE.get());
            tag(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED).add(ModuleCore.ADVANCED.get());
            tag(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME).add(ModuleCore.SUPREME.get());
            tag(BlockTags.DIRT).add(ModuleAgricultureHusbandry.HYDROPONIC_BED.getBlock());

            TagAppender<Block> tTagAppender = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);

            BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Reference.MOD_ID)).forEach(block -> tTagAppender.add(BuiltInRegistries.BLOCK.getResourceKey(block).get()));
        }
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> lookupCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(dataGenerator.getPackOutput(), completableFuture, lookupCompletableFuture, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_PITY, IndustrialTags.Items.MACHINE_FRAME_PITY);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE, IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED, IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME, IndustrialTags.Items.MACHINE_FRAME_SUPREME);

            tag(IndustrialTags.Items.PLASTIC).add(ModuleCore.PLASTIC.get());
            tag(IndustrialTags.Items.SLUDGE_OUTPUT).add(net.minecraft.world.item.Items.DIRT, net.minecraft.world.item.Items.CLAY, net.minecraft.world.item.Items.GRAVEL, net.minecraft.world.item.Items.SAND, net.minecraft.world.item.Items.RED_SAND, net.minecraft.world.item.Items.SOUL_SAND);
            tag(Tags.Items.SLIMEBALLS).add(ModuleCore.PINK_SLIME_ITEM.get());
            tag(IndustrialTags.Items.GEAR_DIAMOND).add(ModuleCore.DIAMOND_GEAR.get());
            tag(IndustrialTags.Items.GEAR_GOLD).add(ModuleCore.GOLD_GEAR.get());
            tag(IndustrialTags.Items.GEAR_IRON).add(ModuleCore.IRON_GEAR.get());
            tag(IndustrialTags.Items.GEARS).add(ModuleCore.DIAMOND_GEAR.get(), ModuleCore.GOLD_GEAR.get(), ModuleCore.IRON_GEAR.get());

            tag(TagUtil.getItemTag(ResourceLocation.parse("c:ingots"))).add(ModuleCore.PINK_SLIME_INGOT.get());
            tag(TagUtil.getItemTag(ResourceLocation.parse("c:ingots/pink_slime"))).add(ModuleCore.PINK_SLIME_INGOT.get());
        }
    }

}
