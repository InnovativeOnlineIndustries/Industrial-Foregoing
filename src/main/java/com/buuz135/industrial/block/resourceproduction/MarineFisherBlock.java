package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.MarineFisherTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class MarineFisherBlock extends IndustrialBlock<MarineFisherTile> {

    public MarineFisherBlock() {
        super("marine_fisher", Properties.from(Blocks.IRON_BLOCK), MarineFisherTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<MarineFisherTile> getTileEntityFactory() {
        return MarineFisherTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pfp").patternLine("bmb").patternLine("grg")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('f', Items.FISHING_ROD)
                .key('b', Items.BUCKET)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .key('g', IndustrialTags.Items.GEAR_IRON)
                .key('r', Items.REDSTONE)
                .build(consumer);
        ;
    }
}
