package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FluidCollectorTile;
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

public class FluidCollectorBlock extends IndustrialBlock<FluidCollectorTile> {

    public FluidCollectorBlock() {
        super("fluid_collector", Properties.from(Blocks.IRON_BLOCK), FluidCollectorTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<FluidCollectorTile> getTileEntityFactory() {
        return FluidCollectorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PBP").patternLine("BMB").patternLine("SRS")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('B', Items.BUCKET)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .key('S', ItemTags.makeWrapperTag("forge:gears/iron"))
                .build(consumer);
    }
}
