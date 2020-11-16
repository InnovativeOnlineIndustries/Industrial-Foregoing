package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.InfinityChargerTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class InfinityChargerBlock extends IndustrialBlock<InfinityChargerTile> {

    public InfinityChargerBlock() {
        super("infinity_charger", Properties.from(Blocks.IRON_BLOCK), InfinityChargerTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<InfinityChargerTile> getTileEntityFactory() {
        return InfinityChargerTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("ppp").patternLine("rcr").patternLine("omo")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('r', Items.REPEATER)
                .key('c', IndustrialTags.Items.GEAR_DIAMOND)
                .key('o', Items.REDSTONE_BLOCK)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .build(consumer);
    }

}
