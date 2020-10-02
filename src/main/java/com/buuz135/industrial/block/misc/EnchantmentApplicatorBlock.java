package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.EnchantmentApplicatorTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class EnchantmentApplicatorBlock extends IndustrialBlock<EnchantmentApplicatorTile> {

    public EnchantmentApplicatorBlock() {
        super("enchantment_applicator", Properties.from(Blocks.IRON_BLOCK), EnchantmentApplicatorTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<EnchantmentApplicatorTile> getTileEntityFactory() {
        return EnchantmentApplicatorTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PPP").patternLine("BMB").patternLine("GBG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('B', Blocks.ANVIL)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('G', ItemTags.makeWrapperTag("forge:gears/gold"))
                .build(consumer);
    }
}
