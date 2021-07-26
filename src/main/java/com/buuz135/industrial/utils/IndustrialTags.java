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
package com.buuz135.industrial.utils;

import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public class IndustrialTags {

    public static class Items {

        public static final Tag<Item> PLASTIC = TagUtil.getItemTag(new ResourceLocation("forge:" + "plastic"));

        public static final Tag<Item> MACHINE_FRAME_PITY = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/pity"));
        public static final Tag<Item> MACHINE_FRAME_SIMPLE = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/simple"));
        public static final Tag<Item> MACHINE_FRAME_ADVANCED = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/advanced"));
        public static final Tag<Item> MACHINE_FRAME_SUPREME = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/supreme"));
        public static final Tag<Item> SLUDGE_OUTPUT = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "sludge"));
        public static final Tag<Item> BIOREACTOR_INPUT = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "bioreactor"));
        public static final Tag<Item> ENCHANTMENT_EXTRACTOR_BLACKLIST = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "enchantment_extractor_blacklist"));

        public static final Tag<Item> GEAR_GOLD = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/gold"));
        public static final Tag<Item> GEAR_DIAMOND = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/diamond"));
        public static final Tag<Item> GEAR_IRON = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/iron"));

        public static final Tag<Item> FERTILIZER = TagUtil.getItemTag(new ResourceLocation("forge:" + "fertilizer"));
    }

    public static class Blocks {

        public static final Tag<Block> MACHINE_FRAME_PITY = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/pity"));
        public static final Tag<Block> MACHINE_FRAME_SIMPLE = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/simple"));
        public static final Tag<Block> MACHINE_FRAME_ADVANCED = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/advanced"));
        public static final Tag<Block> MACHINE_FRAME_SUPREME = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/supreme"));
    }

    public static class EntityTypes {

        public static final Tag<EntityType<?>> MOB_IMPRISONMENT_TOOL_BLACKLIST = TagUtil.getEntityTypeTag(new ResourceLocation(Reference.MOD_ID + ":" + "mob_imprisonment_tool_blacklist"));
    }


    public static class Fluids {

        public static final Tag<Fluid> EXPERIENCE = TagUtil.getFluidTag(new ResourceLocation("forge:experience"));
    }
}
