package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.AnimalFeederTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AnimalFeederBlock extends IndustrialBlock<AnimalFeederTile> {

    public AnimalFeederBlock() {
        super("animal_feeder", Properties.from(Blocks.IRON_BLOCK), AnimalFeederTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<AnimalFeederTile> getTileEntityFactory() {
        return AnimalFeederTile::new;
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
                .key('A', Items.GOLDEN_APPLE)
                .key('C', Items.GOLDEN_CARROT)
                .key('G', new ItemTags.Wrapper(new ResourceLocation("forge:gear/iron")))
                .key('D', Tags.Items.DYES_PURPLE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .build(consumer);
    }
}
