package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.ResourcefulFurnaceTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
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

public class ResourcefulFurnaceBlock extends IndustrialBlock<ResourcefulFurnaceTile> {

    public ResourcefulFurnaceBlock() {
        super("resourceful_furnace", Properties.from(Blocks.IRON_BLOCK), ResourcefulFurnaceTile.class, ModuleResourceProduction.TAB_RESOURCE);
    }

    @Override
    public IFactory<ResourcefulFurnaceTile> getTileEntityFactory() {
        return ResourcefulFurnaceTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PBP").patternLine("LML").patternLine("PRP")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('B', Items.BUCKET)
                .key('L', Items.FURNACE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', new ItemTags.Wrapper(new ResourceLocation("forge:gear/gold")))
                .build(consumer);
    }
}
