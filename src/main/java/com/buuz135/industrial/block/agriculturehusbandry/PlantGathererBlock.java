package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantGathererTile;
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

public class PlantGathererBlock extends IndustrialBlock<PlantGathererTile> {

    public PlantGathererBlock() {
        super("plant_gatherer", Properties.from(Blocks.IRON_BLOCK), PlantGathererTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<PlantGathererTile> getTileEntityFactory() {
        return PlantGathererTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PHP").patternLine("AMA").patternLine("GRG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('H', Items.IRON_HOE)
                .key('A', Items.IRON_AXE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('G', ItemTags.makeWrapperTag("forge:gear/gold"))
                .key('R', Items.REDSTONE)
                .build(consumer);

    }
}
