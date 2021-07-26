package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FermentationStationTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FermentationStationBlock extends IndustrialBlock<FermentationStationTile> {

    public FermentationStationBlock() {
        super("fermentation_station", Properties.copy(Blocks.IRON_BLOCK), FermentationStationTile.class, ModuleResourceProduction.TAB_RESOURCE);
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
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pwp").pattern("wgw").pattern("pbp")
                .define('p', IndustrialTags.Items.PLASTIC)
                .define('w', ItemTags.LOGS)
                .define('g', IndustrialTags.Items.GEAR_GOLD)
                .define('b', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .save(consumer);
    }
}
