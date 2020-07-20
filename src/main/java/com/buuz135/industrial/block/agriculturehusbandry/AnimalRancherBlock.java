package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.AnimalRancherTile;
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

public class AnimalRancherBlock extends IndustrialBlock<AnimalRancherTile> {

    public AnimalRancherBlock() {
        super("animal_rancher", Properties.from(Blocks.IRON_BLOCK), AnimalRancherTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<AnimalRancherTile> getTileEntityFactory() {
        return AnimalRancherTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PPP").patternLine("SBS").patternLine("GMG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('G', ItemTags.makeWrapperTag("forge:gears/gold"))
                .key('S', Items.SHEARS)
                .key('B', Items.BUCKET)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .build(consumer);
    }
}
