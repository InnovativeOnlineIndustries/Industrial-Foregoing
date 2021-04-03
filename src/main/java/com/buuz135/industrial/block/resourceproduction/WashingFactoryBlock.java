package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.WashingFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class WashingFactoryBlock extends IndustrialBlock<WashingFactoryTile> {

    public WashingFactoryBlock() {
        super("washing_factory", Properties.from(Blocks.IRON_BLOCK), WashingFactoryTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<WashingFactoryTile> getTileEntityFactory() {
        return WashingFactoryTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pcp").patternLine("gmg").patternLine("aba")
                .key('g', IndustrialTags.Items.PLASTIC)
                .key('c', ModuleTool.MEAT_FEEDER)
                .key('p', ModuleCore.PINK_SLIME_INGOT)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('b', Blocks.FURNACE)
                .key('a', IndustrialTags.Items.GEAR_DIAMOND)
                .build(consumer);
    }
}
