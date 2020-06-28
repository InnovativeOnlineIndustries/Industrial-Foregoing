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
            func_240522_a_(IndustrialTags.Blocks.MACHINE_FRAME_PITY).func_240534_a_(ModuleCore.PITY);
            func_240522_a_(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE).func_240534_a_(ModuleCore.SIMPLE);
            func_240522_a_(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED).func_240534_a_(ModuleCore.ADVANCED);
            func_240522_a_(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME).func_240534_a_(ModuleCore.SUPREME);
            func_240522_a_(IndustrialTags.Blocks.SAPLING).func_240534_a_(net.minecraft.block.Blocks.ACACIA_SAPLING, net.minecraft.block.Blocks.SPRUCE_SAPLING, net.minecraft.block.Blocks.BIRCH_SAPLING,
                    net.minecraft.block.Blocks.DARK_OAK_SAPLING, net.minecraft.block.Blocks.JUNGLE_SAPLING, net.minecraft.block.Blocks.OAK_SAPLING);
        }
    }

    public static class Items extends ItemTagsProvider {

        public Items(DataGenerator generatorIn) {
            super(generatorIn, new Blocks(generatorIn));
        }

        @Override
        protected void registerTags() {
            this.func_240521_a_(IndustrialTags.Blocks.MACHINE_FRAME_PITY, IndustrialTags.Items.MACHINE_FRAME_PITY);
            this.func_240521_a_(IndustrialTags.Blocks.MACHINE_FRAME_SIMPLE, IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
            this.func_240521_a_(IndustrialTags.Blocks.MACHINE_FRAME_ADVANCED, IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
            this.func_240521_a_(IndustrialTags.Blocks.MACHINE_FRAME_SUPREME, IndustrialTags.Items.MACHINE_FRAME_SUPREME);
            this.func_240521_a_(IndustrialTags.Blocks.SAPLING, IndustrialTags.Items.SAPLING);

            func_240522_a_(IndustrialTags.Items.PLASTIC).func_240534_a_(ModuleCore.PLASTIC);
            func_240522_a_(IndustrialTags.Items.SLUDGE_OUTPUT).func_240534_a_(net.minecraft.item.Items.DIRT, net.minecraft.item.Items.CLAY, net.minecraft.item.Items.GRAVEL, net.minecraft.item.Items.SAND, net.minecraft.item.Items.RED_SAND, net.minecraft.item.Items.SOUL_SAND);
            func_240522_a_(Tags.Items.SLIMEBALLS).func_240534_a_(ModuleCore.PINK_SLIME_ITEM);
        }
    }

}
