package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SlaughterFactoryTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SlaughterFactoryBlock extends IndustrialBlock<SlaughterFactoryTile> {

    public SlaughterFactoryBlock() {
        super("mob_slaughter_factory", Properties.from(Blocks.IRON_BLOCK), SlaughterFactoryTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<SlaughterFactoryTile> getTileEntityFactory() {
        return SlaughterFactoryTile::new;
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
                .key('D', new ItemTags.Wrapper(new ResourceLocation("forge:gear/gold")))
                .key('S', Items.IRON_SWORD)
                .key('A', Items.IRON_AXE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .build(consumer);
    }
}
