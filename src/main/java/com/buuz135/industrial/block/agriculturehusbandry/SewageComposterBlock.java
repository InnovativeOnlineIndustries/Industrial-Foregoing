package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.SewageComposterTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SewageComposterBlock extends IndustrialBlock<SewageComposterTile> {

    public SewageComposterBlock() {
        super("sewage_composter", Properties.from(Blocks.IRON_BLOCK), SewageComposterTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<SewageComposterTile> getTileEntityFactory() {
        return () -> new SewageComposterTile();
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PFP").patternLine("DMD").patternLine("BGB")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('F', Items.FURNACE)
                .key('D', Items.PISTON)
                .key('B', Items.BRICK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('G', ItemTags.makeWrapperTag("forge:gears/iron"))
                .build(consumer);
    }
}
