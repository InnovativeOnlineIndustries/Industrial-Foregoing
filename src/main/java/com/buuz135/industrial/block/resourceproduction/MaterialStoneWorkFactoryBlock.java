package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.MaterialStoneWorkFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MaterialStoneWorkFactoryBlock extends IndustrialBlock<MaterialStoneWorkFactoryTile> {

    public MaterialStoneWorkFactoryBlock() {
        super("material_stonework_factory", Properties.from(Blocks.IRON_BLOCK), MaterialStoneWorkFactoryTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<MaterialStoneWorkFactoryTile> getTileEntityFactory() {
        return () -> new MaterialStoneWorkFactoryTile();
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("pcp").patternLine("gmf").patternLine("aba")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('c', Blocks.CRAFTING_TABLE)
                .key('g', Items.DIAMOND_PICKAXE)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('f', Blocks.FURNACE)
                .key('a', IndustrialTags.Items.GEAR_GOLD)
                .key('b', ModuleCore.PINK_SLIME_ITEM)
                .build(consumer);
    }
}
