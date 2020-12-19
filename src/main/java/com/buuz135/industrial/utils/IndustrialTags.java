package com.buuz135.industrial.utils;

import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class IndustrialTags {

    public static class Items {

        public static final ITag<Item> PLASTIC = TagUtil.getItemTag(new ResourceLocation("forge:" + "plastic"));
        public static final ITag<Item> SAPLING = TagUtil.getItemTag(new ResourceLocation("forge:" + "sapling"));

        public static final ITag<Item> MACHINE_FRAME_PITY = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/pity"));
        public static final ITag<Item> MACHINE_FRAME_SIMPLE = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/simple"));
        public static final ITag<Item> MACHINE_FRAME_ADVANCED = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/advanced"));
        public static final ITag<Item> MACHINE_FRAME_SUPREME = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/supreme"));
        public static final ITag<Item> SLUDGE_OUTPUT = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "sludge"));
        public static final ITag<Item> BIOREACTOR_INPUT = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "bioreactor"));
        public static final ITag<Item> ENCHANTMENT_EXTRACTOR_BLACKLIST = TagUtil.getItemTag(new ResourceLocation(Reference.MOD_ID + ":" + "enchantment_extractor_blacklist"));

        public static final ITag<Item> GEAR_GOLD = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/gold"));
        public static final ITag<Item> GEAR_DIAMOND = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/diamond"));
        public static final ITag<Item> GEAR_IRON = TagUtil.getItemTag(new ResourceLocation("forge:" + "gears/iron"));

        public static final ITag<Item> FERTILIZER = TagUtil.getItemTag(new ResourceLocation("forge:" + "fertilizer"));
    }

    public static class Blocks {

        public static final ITag<Block> SAPLING = TagUtil.getBlockTag(new ResourceLocation("forge:" + "sapling"));

        public static final ITag<Block> MACHINE_FRAME_PITY = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/pity"));
        public static final ITag<Block> MACHINE_FRAME_SIMPLE = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/simple"));
        public static final ITag<Block> MACHINE_FRAME_ADVANCED = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/advanced"));
        public static final ITag<Block> MACHINE_FRAME_SUPREME = TagUtil.getBlockTag(new ResourceLocation(Reference.MOD_ID + ":" + "machine_frame/supreme"));
    }

    public static class EntityTypes {

        public static final ITag<EntityType<?>> MOB_IMPRISONMENT_TOOL_BLACKLIST = TagUtil.getEntityTypeTag(new ResourceLocation(Reference.MOD_ID + ":" + "mob_imprisonment_tool_blacklist"));
    }

}
