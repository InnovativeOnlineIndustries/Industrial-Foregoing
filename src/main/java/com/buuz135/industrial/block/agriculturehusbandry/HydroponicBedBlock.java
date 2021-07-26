package com.buuz135.industrial.block.agriculturehusbandry;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculturehusbandry.tile.HydroponicBedTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.function.Consumer;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class HydroponicBedBlock extends IndustrialBlock<HydroponicBedTile> {

    public HydroponicBedBlock() {
        super("hydroponic_bed", Properties.copy(Blocks.IRON_BLOCK), HydroponicBedTile.class, ModuleAgricultureHusbandry.TAB_AG_HUS);
    }

    @Override
    public IFactory<HydroponicBedTile> getTileEntityFactory() {
        return HydroponicBedTile::new;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType type = plantable.getPlantType(world, pos);
        return type == PlantType.CROP || type == PlantType.NETHER || type == PlantType.BEACH || type == PlantType.DESERT;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PDP").pattern("SBS").pattern("GMG")
                .define('P', IndustrialTags.Items.PLASTIC)
                .define('D', Blocks.DIRT)
                .define('G', IndustrialTags.Items.GEAR_GOLD)
                .define('S', Items.IRON_HOE)
                .define('B', ModuleCore.FERTILIZER)
                .define('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
                .save(consumer);
    }
}
