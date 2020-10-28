package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.OreLaserBaseTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class OreLaserBaseBlock extends IndustrialBlock<OreLaserBaseTile> {

    public OreLaserBaseBlock() {
        super("ore_laser_base", Properties.from(Blocks.IRON_BLOCK), OreLaserBaseTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<OreLaserBaseTile> getTileEntityFactory() {
        return OreLaserBaseTile::new;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pfp").patternLine("bmb").patternLine("grg")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('f', Items.DIAMOND_PICKAXE)
                .key('b', Items.IRON_ORE)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('g', IndustrialTags.Items.GEAR_DIAMOND)
                .key('r', Items.REDSTONE)
                .build(consumer);
    }
}
