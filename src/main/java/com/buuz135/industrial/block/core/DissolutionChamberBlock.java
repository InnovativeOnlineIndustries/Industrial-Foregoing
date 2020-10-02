package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.core.tile.DissolutionChamberTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class DissolutionChamberBlock extends IndustrialBlock<DissolutionChamberTile> {

    public DissolutionChamberBlock() {
        super("dissolution_chamber", Properties.from(Blocks.IRON_BLOCK), DissolutionChamberTile.class, ModuleCore.TAB_CORE);
    }

    @Override
    public IFactory<DissolutionChamberTile> getTileEntityFactory() {
        return DissolutionChamberTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PCP").patternLine("BMB").patternLine("GDG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('C', Tags.Items.CHESTS)
                .key('B', Items.BUCKET)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('G', Tags.Items.INGOTS_GOLD)
                .key('D', ItemTags.makeWrapperTag("forge:gears/diamond"))
                .build(consumer);
    }
}
