package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.BlockBreakerTile;
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

public class BlockBreakerBlock extends IndustrialBlock<BlockBreakerTile> {

    public BlockBreakerBlock() {
        super("block_breaker", Properties.from(Blocks.IRON_BLOCK), BlockBreakerTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<BlockBreakerTile> getTileEntityFactory() {
        return BlockBreakerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PGP").patternLine("IMD").patternLine("SRS")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('I', Items.IRON_PICKAXE)
                .key('D', Items.IRON_SHOVEL)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .key('G', ItemTags.makeWrapperTag("forge:gears/gold"))
                .key('S', ItemTags.makeWrapperTag("forge:gears/iron"))
                .build(consumer);
    }
}
