package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.PotionBrewerTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PotionBrewerBlock extends IndustrialBlock<PotionBrewerTile> {

    public PotionBrewerBlock() {
        super("potion_brewer", Properties.from(Blocks.IRON_BLOCK), PotionBrewerTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<PotionBrewerTile> getTileEntityFactory() {
        return PotionBrewerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PSP").patternLine("BMB").patternLine("GBG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('S', Blocks.BREWING_STAND)
                .key('B', ItemTags.makeWrapperTag("forge:gears/gold"))
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('G', Items.REPEATER)
                .build(consumer);
    }
}
