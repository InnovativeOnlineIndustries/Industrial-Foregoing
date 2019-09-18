package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.tile.PitifulGeneratorTile;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PitifulGeneratorBlock extends IndustrialBlock<PitifulGeneratorTile> {

    public PitifulGeneratorBlock(ItemGroup group) {
        super("pitiful_generator", Properties.from(Blocks.COBBLESTONE), PitifulGeneratorTile.class);
        this.setItemGroup(group);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<PitifulGeneratorTile> getTileEntityFactory() {
        return PitifulGeneratorTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pdp").patternLine("gmg").patternLine("pfp")
                .key('p', Blocks.COBBLESTONE)
                .key('d', Tags.Items.INGOTS_GOLD)
                .key('g', Blocks.IRON_BARS)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('f', Blocks.FURNACE)
                .build(consumer);
    }
}
