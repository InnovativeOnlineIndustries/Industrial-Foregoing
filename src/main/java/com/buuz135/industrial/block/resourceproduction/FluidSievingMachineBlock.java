package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FluidSievingMachineTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FluidSievingMachineBlock extends IndustrialBlock<FluidSievingMachineTile> {

    public FluidSievingMachineBlock() {
        super("fluid_sieving_machine", BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK), FluidSievingMachineTile.class, ModuleResourceProduction.TAB_RESOURCE);
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
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).pattern("pcp").pattern("ggg").pattern("aba")
                .define('p', IndustrialTags.Items.PLASTIC)
                .define('c', ModuleCore.PINK_SLIME_ITEM)
                .define('g', Items.IRON_BARS)
                .define('b', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .define('a', IndustrialTags.Items.GEAR_GOLD)
                .save(consumer);
    }

}
