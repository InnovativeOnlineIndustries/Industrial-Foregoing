package com.buuz135.industrial.recipe.provider;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

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

            getBuilder(IndustrialTags.Items.PLASTIC).add(ModuleCore.PLASTIC);
        }
    }

}
