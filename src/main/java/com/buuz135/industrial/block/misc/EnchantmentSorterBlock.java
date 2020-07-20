package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentSorterTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class EnchantmentSorterBlock extends IndustrialBlock<EnchantmentSorterTile> {

    public EnchantmentSorterBlock() {
        super("enchantment_sorter", Properties.from(Blocks.IRON_BLOCK), EnchantmentSorterTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<EnchantmentSorterTile> getTileEntityFactory() {
        return EnchantmentSorterTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PSP").patternLine("BME").patternLine("PGP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('S', Items.ENDER_PEARL)
                .key('B', Items.BOOK)
                .key('E', Items.ENCHANTED_BOOK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('G', ItemTags.makeWrapperTag("forge:gears/diamond"))
                .build(consumer);
    }

}
