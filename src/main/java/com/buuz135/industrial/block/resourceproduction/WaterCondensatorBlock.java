package com.buuz135.industrial.block.resourceproduction;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.resourceproduction.tile.WaterCondensatorTile;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class WaterCondensatorBlock extends IndustrialBlock<WaterCondensatorTile> {

    public WaterCondensatorBlock() {
        super("water_condensator", Properties.from(Blocks.IRON_BLOCK), WaterCondensatorTile.class, ModuleResourceProduction.TAB_RESOURCE);

    }

    @Override
    public IFactory<WaterCondensatorTile> getTileEntityFactory() {
        return WaterCondensatorTile::new;
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
                .key('B', Items.WATER_BUCKET)
                .key('L', Items.PISTON)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('R', Items.REDSTONE)
                .key('G', ItemTags.makeWrapperTag("forge:gears/iron"))
                .build(consumer);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("text.industrialforegoing.tooltip.power_optional").mergeStyle(TextFormatting.GOLD));
    }
}
