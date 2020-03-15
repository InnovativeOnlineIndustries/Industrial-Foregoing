package com.buuz135.industrial.block.misc;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.misc.tile.StasisChamberTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class StasisChamberBlock extends IndustrialBlock<StasisChamberTile> {

    public StasisChamberBlock() {
        super("stasis_chamber", Properties.from(Blocks.IRON_BLOCK), StasisChamberTile.class, ModuleMisc.TAB_MISC);
    }

    @Override
    public IFactory<StasisChamberTile> getTileEntityFactory() {
        return StasisChamberTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this).patternLine("sss").patternLine("gmg").patternLine("ipi")
                .key('s', Blocks.SOUL_SAND)
                .key('g', Items.GHAST_TEAR)
                .key('m', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .key('i', IndustrialTags.Items.GEAR_GOLD)
                .key('p', Blocks.PISTON)
                .build(consumer);
    }
}
