package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.AnimalBabySeparatorTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AnimalBabySeparatorBlock extends IndustrialBlock<AnimalBabySeparatorTile> {

    public AnimalBabySeparatorBlock() {
        super("animal_baby_separator", Properties.from(Blocks.IRON_BLOCK), AnimalBabySeparatorTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<AnimalBabySeparatorTile> getTileEntityFactory() {
        return AnimalBabySeparatorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PAP").patternLine("CMC").patternLine("DGD")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('A', Items.GOLDEN_CARROT)
                .key('C', Items.WHEAT)
                .key('G', ItemTags.makeWrapperTag("forge:gear/gold"))
                .key('D', Tags.Items.DYES_PURPLE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .build(consumer);
    }
}
