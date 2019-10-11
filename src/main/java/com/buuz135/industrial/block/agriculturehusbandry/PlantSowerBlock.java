package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.PlantSowerTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PlantSowerBlock extends IndustrialBlock<PlantSowerTile> {

    public PlantSowerBlock() {
        super("plant_sower", Properties.from(Blocks.IRON_BLOCK), PlantSowerTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<PlantSowerTile> getTileEntityFactory() {
        return PlantSowerTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PBP").patternLine("LML").patternLine("GRG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('B', Items.FLOWER_POT)
                .key('L', Items.PISTON)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .key('G', new ItemTags.Wrapper(new ResourceLocation("forge:gear/iron")))
                .build(consumer);
    }
}
