package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentFactoryTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class EnchantmentFactoryBlock extends IndustrialBlock<EnchantmentFactoryTile> {

    public EnchantmentFactoryBlock() {
        super("enchantment_factory", AbstractBlock.Properties.from(Blocks.IRON_BLOCK), EnchantmentFactoryTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<EnchantmentFactoryTile> getTileEntityFactory() {
        return EnchantmentFactoryTile::new;
    }

    @Override
    public RotatableBlock.RotationType getRotationType() {
        return RotatableBlock.RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PBP").patternLine("DMD").patternLine("OOO")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', Items.DIAMOND)
                .key('B', Items.BOOK)
                .key('O', Blocks.OBSIDIAN)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .build(consumer);
    }
}
