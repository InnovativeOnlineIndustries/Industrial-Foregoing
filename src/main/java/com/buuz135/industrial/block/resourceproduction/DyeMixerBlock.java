package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.DyeMixerTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class DyeMixerBlock extends IndustrialBlock<DyeMixerTile> {

    public DyeMixerBlock() {
        super("dye_mixer", Properties.from(Blocks.IRON_BLOCK), DyeMixerTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<DyeMixerTile> getTileEntityFactory() {
        return DyeMixerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PDP").patternLine("DMD").patternLine("PRP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', Tags.Items.DYES)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', new ItemTags.Wrapper(new ResourceLocation("forge:gear/gold")))
                .build(consumer);
    }
}
