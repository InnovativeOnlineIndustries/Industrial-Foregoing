package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.BlockPlacerTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockPlacerBlock extends IndustrialBlock<BlockPlacerTile> {

    public BlockPlacerBlock() {
        super("block_placer", Properties.from(Blocks.IRON_BLOCK), BlockPlacerTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public IFactory<BlockPlacerTile> getTileEntityFactory() {
        return BlockPlacerTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PDP").patternLine("DMD").patternLine("PRP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', Blocks.DROPPER)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .build(consumer);
    }
}
