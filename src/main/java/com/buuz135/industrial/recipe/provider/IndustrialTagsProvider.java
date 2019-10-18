package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.Tags;

public class IndustrialTagsProvider {

    public static class Blocks extends BlockTagsProvider {

        public Blocks(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {
            getBuilder(IndustrialTags.Blocks.MACHINE_FRAME_PITY).add(ModuleCore.PITY);
            getBuilder(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE).add(ModuleCore.SIMPLE);
            getBuilder(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED).add(ModuleCore.ADVANCED);
            getBuilder(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME).add(ModuleCore.SUPREME);
            getBuilder(IndustrialTags.Blocks.SAPLING).add(net.minecraft.block.Blocks.ACACIA_SAPLING, net.minecraft.block.Blocks.SPRUCE_SAPLING, net.minecraft.block.Blocks.BIRCH_SAPLING,
                    net.minecraft.block.Blocks.DARK_OAK_SAPLING, net.minecraft.block.Blocks.JUNGLE_SAPLING, net.minecraft.block.Blocks.OAK_SAPLING);
        }
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerTags() {
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_PITY, IndustrialTags.Items.MACHINE_FRAME_PITY);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE, IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED, IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
            this.copy(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME, IndustrialTags.Items.MACHINE_FRAME_SUPREME);
            this.copy(IndustrialTags.Blocks.SAPLING, IndustrialTags.Items.SAPLING);

            getBuilder(IndustrialTags.Items.PLASTIC).add(ModuleCore.PLASTIC);
            getBuilder(IndustrialTags.Items.SLUDGE_OUTPUT).add(net.minecraft.item.Items.DIRT, net.minecraft.item.Items.CLAY, net.minecraft.item.Items.GRAVEL, net.minecraft.item.Items.SAND, net.minecraft.item.Items.RED_SAND, net.minecraft.item.Items.SOUL_SAND);
            getBuilder(Tags.Items.SLIMEBALLS).add(ModuleCore.PINK_SLIME_ITEM);
        }
    }

}
