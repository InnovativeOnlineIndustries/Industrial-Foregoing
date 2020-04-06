package com.buuz135.industrial.block.mob;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.mob.tile.MobDetectorTile;
import com.buuz135.industrial.module.ModuleMob;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MobDetectorBlock extends IndustrialBlock<MobDetectorTile> {

    private int redstoneSignal = 0;

    public MobDetectorBlock() {
        super("mob_detector", Properties.from(Blocks.IRON_BLOCK), MobDetectorTile.class, ModuleMob.TAB_MOB);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        Direction facing = blockState.get(RotatableBlock.FACING_HORIZONTAL);
        if (side != facing.getOpposite()) {
            return redstoneSignal;
        }
        return 0;
    }

    @Override
    public IFactory<MobDetectorTile> getTileEntityFactory() {
        return MobDetectorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
            .patternLine("ppp").patternLine("rcr").patternLine("omo")
            .key('p', IndustrialTags.Items.PLASTIC)
            .key('r', Items.REPEATER)
            .key('c', Items.COMPARATOR)
            .key('o', Items.OBSERVER)
            .key('m', IndustrialTags.Items.MACHINE_FRAME_SIMPLE)
            .build(consumer);
    }

    public void setRedstoneSignal(int redstoneSignal) {
        this.redstoneSignal = redstoneSignal;
    }
}
