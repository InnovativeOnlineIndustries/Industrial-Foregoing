package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.FluidLaserBaseTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class FluidLaserBaseBlock extends IndustrialBlock<FluidLaserBaseTile> {

    public FluidLaserBaseBlock() {
        super("fluid_laser_base", Properties.from(Blocks.IRON_BLOCK), FluidLaserBaseTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<FluidLaserBaseTile> getTileEntityFactory() {
        return FluidLaserBaseTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pfp").patternLine("bmb").patternLine("grg")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('f', Items.DIAMOND_PICKAXE)
                .key('b', Items.BUCKET)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('g', IndustrialTags.Items.GEAR_DIAMOND)
                .key('r', Items.REDSTONE)
                .build(consumer);
    }
}
