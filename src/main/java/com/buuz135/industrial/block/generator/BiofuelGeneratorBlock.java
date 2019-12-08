package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.tile.BiofuelGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BiofuelGeneratorBlock extends IndustrialBlock<BiofuelGeneratorTile> {

    public BiofuelGeneratorBlock() {
        super("biofuel_generator", Properties.from(Blocks.IRON_BLOCK), BiofuelGeneratorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Override
    public IFactory<BiofuelGeneratorTile> getTileEntityFactory() {
        return BiofuelGeneratorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PDP").patternLine("SMS").patternLine("ASA")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', Blocks.FURNACE)
                .key('S', Blocks.PISTON)
                .key('A', new ItemTags.Wrapper(new ResourceLocation("forge:gear/gold")))
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .build(consumer);
    }
}
