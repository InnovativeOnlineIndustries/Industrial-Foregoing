package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentExtractorTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class EnchantmentExtractorBlock extends IndustrialBlock<EnchantmentExtractorTile> {

    public EnchantmentExtractorBlock() {
        super("enchantment_extractor", Properties.from(Blocks.IRON_BLOCK), EnchantmentExtractorTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<EnchantmentExtractorTile> getTileEntityFactory() {
        return EnchantmentExtractorTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PSP").patternLine("BMB").patternLine("DGD")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('S', Blocks.NETHER_BRICKS)
                .key('B', Items.BOOK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('G', ItemTags.makeWrapperTag("forge:gears/gold"))
                .key('D', Items.DIAMOND)
                .build(consumer);
    }

}
