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

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IndustrialTagsProvider {

    public static class Blocks extends BlockTagsProvider {

        public Blocks(DataGenerator generatorIn, String modid, ExistingFileHelper helper) {
            super(generatorIn, modid, helper);
        }

        @Override
        protected void addTags() {
            tag((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_PITY).add(ModuleCore.PITY);
            tag((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE).add(ModuleCore.SIMPLE);
            tag((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED).add(ModuleCore.ADVANCED);
            tag((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SUPREME).add(ModuleCore.SUPREME);
        }
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator generatorIn, String modid, ExistingFileHelper helper) {
            super(generatorIn, new Blocks(generatorIn, modid, helper), modid, helper);
        }

        @Override
        protected void addTags() {
            this.copy((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_PITY, (Tag.Named<Item>) IndustrialTags.Items.MACHINE_FRAME_PITY);
            this.copy((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE, (Tag.Named<Item>) IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
            this.copy((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED, (Tag.Named<Item>) IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
            this.copy((Tag.Named<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SUPREME, (Tag.Named<Item>) IndustrialTags.Items.MACHINE_FRAME_SUPREME);

            tag((Tag.Named<Item>) IndustrialTags.Items.PLASTIC).add(ModuleCore.PLASTIC);
            tag((Tag.Named<Item>) IndustrialTags.Items.SLUDGE_OUTPUT).add(net.minecraft.world.item.Items.DIRT, net.minecraft.world.item.Items.CLAY, net.minecraft.world.item.Items.GRAVEL, net.minecraft.world.item.Items.SAND, net.minecraft.world.item.Items.RED_SAND, net.minecraft.world.item.Items.SOUL_SAND);
            tag(Tags.Items.SLIMEBALLS).add(ModuleCore.PINK_SLIME_ITEM);
        }
    }

}
