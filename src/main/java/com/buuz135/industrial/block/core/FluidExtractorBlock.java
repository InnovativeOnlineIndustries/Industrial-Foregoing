package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FluidExtractorBlock extends BlockRotation<FluidExtractorTile> {

    public FluidExtractorBlock() {
        super("fluid_extractor", Properties.from(Blocks.STONE), FluidExtractorTile.class);
    }

    @Override
    public IFactory<FluidExtractorTile> getTileEntityFactory() {
        return FluidExtractorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("IGI").patternLine("CMC").patternLine("IPI")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('G', Items.LIGHT_WEIGHTED_PRESSURE_PLATE)
                .key('C', Tags.Items.COBBLESTONE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('P', Blocks.PISTON)
                .build(consumer);
    }
}
