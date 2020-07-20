package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SewerTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SewerBlock extends IndustrialBlock<SewerTile> {

    public SewerBlock() {
        super("sewer", Properties.from(Blocks.IRON_BLOCK), SewerTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<SewerTile> getTileEntityFactory() {
        return () -> new SewerTile();
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PEP").patternLine("BMB").patternLine("BGB")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('E', Items.BUCKET)
                .key('B', Items.BRICK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('G', ItemTags.makeWrapperTag("forge:gears/iron"))
                .build(consumer);
    }
}
