package com.buuz135.industrial.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class IndustrialTags {

    public static class Items {

        public static final Tag<Item> PLASTIC = new ItemTags.Wrapper(new ResourceLocation("forge", "plastic"));
        public static final Tag<Item> MACHINE_FRAME_PITY = new ItemTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/pity"));
        public static final Tag<Item> MACHINE_FRAME_SIMPLE = new ItemTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/simple"));
        public static final Tag<Item> MACHINE_FRAME_ADVANCED = new ItemTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/advanced"));
        public static final Tag<Item> MACHINE_FRAME_SUPREME = new ItemTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/supreme"));
        public static final Tag<Item> SLUDGE_OUTPUT = new ItemTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "sludge"));

    }

    public static class Blocks {

        public static final Tag<Block> MACHINE_FRAME_PITY = new BlockTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/pity"));
        public static final Tag<Block> MACHINE_FRAME_SIMPLE = new BlockTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/simple"));
        public static final Tag<Block> MACHINE_FRAME_ADVANCED = new BlockTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/advanced"));
        public static final Tag<Block> MACHINE_FRAME_SUPREME = new BlockTags.Wrapper(new ResourceLocation(Reference.MOD_ID, "machine_frame/supreme"));
    }

}
