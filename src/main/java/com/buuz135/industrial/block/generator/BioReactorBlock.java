package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.tile.BioReactorTile;
import com.buuz135.industrial.module.ModuleGenerator;
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

public class BioReactorBlock extends IndustrialBlock<BioReactorTile> {

    public BioReactorBlock() {
        super("bioreactor", Properties.from(Blocks.IRON_BLOCK), BioReactorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Override
    public IFactory<BioReactorTile> getTileEntityFactory() {
        return BioReactorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PDP").patternLine("SMS").patternLine("ARA")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', new ItemTags.Wrapper(new ResourceLocation("forge:gear/diamond")))
                .key('S', Tags.Items.SLIMEBALLS)
                .key('A', Items.BRICK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.SUGAR)
                .build(consumer);
    }
}
