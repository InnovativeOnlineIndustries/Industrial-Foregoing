package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FermentationStationTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FermentationStationBlock extends IndustrialBlock<FermentationStationTile> {

    public FermentationStationBlock() {
        super("fermentation_station", Properties.from(Blocks.IRON_BLOCK), FermentationStationTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<FermentationStationTile> getTileEntityFactory() {
        return FermentationStationTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pwp").patternLine("wgw").patternLine("pbp")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('w', ItemTags.LOGS)
                .key('g', IndustrialTags.Items.GEAR_GOLD)
                .key('b', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .build(consumer);
    }
}
