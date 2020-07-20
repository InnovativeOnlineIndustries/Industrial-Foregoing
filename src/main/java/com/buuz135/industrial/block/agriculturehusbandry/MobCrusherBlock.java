package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.MobCrusherTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class MobCrusherBlock extends IndustrialBlock<MobCrusherTile> {

    public MobCrusherBlock() {
        super("mob_crusher", Properties.from(Blocks.IRON_BLOCK), MobCrusherTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<MobCrusherTile> getTileEntityFactory() {
        return MobCrusherTile::new;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PSP").patternLine("BMB").patternLine("GRG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('S', Items.IRON_SWORD)
                .key('B', Items.BOOK)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED)
                .key('R', Items.REDSTONE)
                .key('G', ItemTags.makeWrapperTag("forge:gears/gold"))
                .build(consumer);
    }
}
