package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.WashingFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import com.hrznstudio.titanium.block.RotatableBlock.RotationType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WashingFactoryBlock extends IndustrialBlock<WashingFactoryTile> {

    public WashingFactoryBlock() {
        super("washing_factory", Properties.copy(Blocks.IRON_BLOCK), WashingFactoryTile.class, ModuleResourceProduction.TAB_RESOURCE);
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
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pcp").pattern("gmg").pattern("aba")
                .define('g', IndustrialTags.Items.PLASTIC)
                .define('c', ModuleTool.MEAT_FEEDER)
                .define('p', ModuleCore.PINK_SLIME_INGOT)
                .define('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .define('b', Blocks.FURNACE)
                .define('a', IndustrialTags.Items.GEAR_DIAMOND)
                .save(consumer);
    }
}
