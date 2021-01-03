package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;

public class IndustrialTagsProvider {

    public static class Blocks extends BlockTagsProvider {

        public Blocks(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {
            getOrCreateBuilder((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_PITY).add(ModuleCore.PITY);
            getOrCreateBuilder((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE).add(ModuleCore.SIMPLE);
            getOrCreateBuilder((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED).add(ModuleCore.ADVANCED);
            getOrCreateBuilder((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SUPREME).add(ModuleCore.SUPREME);
        }
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator generatorIn) {
            super(generatorIn, new Blocks(generatorIn));
        }

        @Override
        protected void registerTags() {
            this.copy((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_PITY, (ITag.INamedTag<Item>) IndustrialTags.Items.MACHINE_FRAME_PITY);
            this.copy((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE, (ITag.INamedTag<Item>) IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
            this.copy((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED, (ITag.INamedTag<Item>) IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
            this.copy((ITag.INamedTag<Block>) IndustrialTags.Blocks.MACHINE_FRAME_SUPREME, (ITag.INamedTag<Item>) IndustrialTags.Items.MACHINE_FRAME_SUPREME);

            getOrCreateBuilder((ITag.INamedTag<Item>) IndustrialTags.Items.PLASTIC).add(ModuleCore.PLASTIC);
            getOrCreateBuilder((ITag.INamedTag<Item>) IndustrialTags.Items.SLUDGE_OUTPUT).add(net.minecraft.item.Items.DIRT, net.minecraft.item.Items.CLAY, net.minecraft.item.Items.GRAVEL, net.minecraft.item.Items.SAND, net.minecraft.item.Items.RED_SAND, net.minecraft.item.Items.SOUL_SAND);
            getOrCreateBuilder(Tags.Items.SLIMEBALLS).add(ModuleCore.PINK_SLIME_ITEM);
        }
    }

}
