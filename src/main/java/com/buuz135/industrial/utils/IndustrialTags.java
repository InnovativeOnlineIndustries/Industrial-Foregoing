package com.buuz135.industrial.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class IndustrialTags {

    public static class Items {

        public static final ITag.INamedTag<Item> PLASTIC = ItemTags.makeWrapperTag("forge:" + "plastic");
        public static final ITag.INamedTag<Item> SAPLING = ItemTags.makeWrapperTag("forge:" + "sapling");

        public static final ITag.INamedTag<Item> MACHINE_FRAME_PITY = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/pity");
        public static final ITag.INamedTag<Item> MACHINE_FRAME_SIMPLE = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/simple");
        public static final ITag.INamedTag<Item> MACHINE_FRAME_ADVANCED = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/advanced");
        public static final ITag.INamedTag<Item> MACHINE_FRAME_SUPREME = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/supreme");
        public static final ITag.INamedTag<Item> SLUDGE_OUTPUT = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "sludge");
        public static final ITag.INamedTag<Item> BIOREACTOR_INPUT = ItemTags.makeWrapperTag(Reference.MOD_ID + ":" + "bioreactor");

        public static final ITag.INamedTag<Item> GEAR_GOLD = ItemTags.makeWrapperTag("forge:" + "gear/gold");
        public static final ITag.INamedTag<Item> GEAR_DIAMOND = ItemTags.makeWrapperTag("forge:" + "gear/diamond");
        public static final ITag.INamedTag<Item> GEAR_IRON = ItemTags.makeWrapperTag("forge:" + "gear/iron");

        public static final ITag.INamedTag<Item> FERTILIZER = ItemTags.makeWrapperTag("forge:" + "fertilizer");
    }

    public static class Blocks {

        public static final ITag.INamedTag<Block> SAPLING = BlockTags.makeWrapperTag("forge:" + "sapling");

        public static final ITag.INamedTag<Block> MACHINE_FRAME_PITY = BlockTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/pity");
        public static final ITag.INamedTag<Block> MACHINE_FRAME_SIMPLE = BlockTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/simple");
        public static final ITag.INamedTag<Block> MACHINE_FRAME_ADVANCED = BlockTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/advanced");
        public static final ITag.INamedTag<Block> MACHINE_FRAME_SUPREME = BlockTags.makeWrapperTag(Reference.MOD_ID + ":" + "machine_frame/supreme");
    }

}
