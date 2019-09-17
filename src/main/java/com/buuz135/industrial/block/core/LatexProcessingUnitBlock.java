package com.buuz135.industrial.block.core;

import com.buuz135.industrial.block.core.tile.LatexProcessingUnitTile;
import com.buuz135.industrial.config.MachineCoreConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@ConfigFile.Child(MachineCoreConfig.class)
public class LatexProcessingUnitBlock extends BlockRotation<LatexProcessingUnitTile> {

    @ConfigVal(comment = "Power consumed every tick when the machine is working")
    public static int POWER_CONSUMED_EVERY_TICK = 20;

    public LatexProcessingUnitBlock() {
        super("latex_processing_unit", Properties.from(Blocks.STONE), LatexProcessingUnitTile.class);
    }

    @Override
    public IFactory<LatexProcessingUnitTile> getTileEntityFactory() {
        return LatexProcessingUnitTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("IGI").patternLine("BML").patternLine("IFI")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('G', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .key('B', Items.WATER_BUCKET)
                .key('L', ModuleCore.LATEX.getBucketFluid())
                .key('M', IndustrialTags.Items.MACHINE_FRAME_PITY)
                .key('F', Blocks.FURNACE)
                .build(consumer);
    }
}
