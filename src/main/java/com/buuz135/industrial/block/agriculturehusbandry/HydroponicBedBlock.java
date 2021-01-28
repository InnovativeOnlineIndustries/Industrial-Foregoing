package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.HydroponicBedTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.function.Consumer;

public class HydroponicBedBlock extends IndustrialBlock<HydroponicBedTile> {

    public HydroponicBedBlock() {
        super("hydroponic_bed", Properties.from(Blocks.IRON_BLOCK), HydroponicBedTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<HydroponicBedTile> getTileEntityFactory() {
        return HydroponicBedTile::new;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos);
        return type == PlantType.CROP || type == PlantType.NETHER || type == PlantType.BEACH || type == PlantType.DESERT;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .patternLine("PDP").patternLine("SBS").patternLine("GMG")
                .key('P', IndustrialTags.Items.PLASTIC)
                .key('D', Blocks.DIRT)
                .key('G', IndustrialTags.Items.GEAR_GOLD)
                .key('S', Items.IRON_HOE)
                .key('B', ModuleCore.FERTILIZER)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .build(consumer);
    }
}
