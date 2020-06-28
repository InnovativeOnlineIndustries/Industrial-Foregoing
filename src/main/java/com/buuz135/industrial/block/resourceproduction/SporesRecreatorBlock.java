package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.SporesRecreatorTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SporesRecreatorBlock extends IndustrialBlock<SporesRecreatorTile> {

    public SporesRecreatorBlock() {
        super("spores_recreator", Properties.from(Blocks.IRON_BLOCK), SporesRecreatorTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<SporesRecreatorTile> getTileEntityFactory() {
        return SporesRecreatorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PSP").patternLine("IMI").patternLine("PSP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('I', Tags.Items.MUSHROOMS)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('S', ItemTags.makeWrapperTag("forge:gear/iron"))
                .build(consumer);
    }
}
