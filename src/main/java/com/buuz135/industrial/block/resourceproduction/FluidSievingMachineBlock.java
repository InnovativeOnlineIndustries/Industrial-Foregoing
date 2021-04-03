package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FluidSievingMachineTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FluidSievingMachineBlock extends IndustrialBlock<FluidSievingMachineTile> {

    public FluidSievingMachineBlock() {
        super("fluid_sieving_machine", AbstractBlock.Properties.from(Blocks.IRON_BLOCK), FluidSievingMachineTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<FluidSievingMachineTile> getTileEntityFactory() {
        return FluidSievingMachineTile::new;
    }

    @Nonnull
    @Override
    public RotatableBlock.RotationType getRotationType() {
        return RotatableBlock.RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pcp").patternLine("ggg").patternLine("aba")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('c', ModuleCore.PINK_SLIME_ITEM)
                .key('g', Items.IRON_BARS)
                .key('b', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('a', IndustrialTags.Items.GEAR_GOLD)
                .build(consumer);
    }

}
