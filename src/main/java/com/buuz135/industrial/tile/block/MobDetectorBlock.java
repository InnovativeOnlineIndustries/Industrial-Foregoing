package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.mob.MobDetectorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import javax.annotation.Nullable;

public class MobDetectorBlock extends CustomOrientedBlock<MobDetectorTile> {

    public MobDetectorBlock() {
        super("mob_detector", MobDetectorTile.class, Material.ROCK, 100, 10);
    }


    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof MobDetectorTile) {
            MobDetectorTile tile = (MobDetectorTile) world.getTileEntity(pos);
            return side.equals(tile.getFacing().getOpposite());
        }
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (blockAccess.getTileEntity(pos) != null && blockAccess.getTileEntity(pos) instanceof MobDetectorTile) {
            MobDetectorTile tile = (MobDetectorTile) blockAccess.getTileEntity(pos);
            if (side.equals(tile.getFacing().getOpposite())) {
                return tile.getRedstoneSignal();
            }
        }
        return super.getStrongPower(blockState, blockAccess, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (blockAccess.getTileEntity(pos) != null && blockAccess.getTileEntity(pos) instanceof MobDetectorTile) {
            MobDetectorTile tile = (MobDetectorTile) blockAccess.getTileEntity(pos);
            if (side.equals(tile.getFacing().getOpposite())) {
                return tile.getRedstoneSignal();
            }
        }
        return super.getStrongPower(blockState, blockAccess, pos, side);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "rcr", "imi",
                'p', ItemRegistry.plastic,
                'r', Items.REPEATER,
                'c', Items.COMPARATOR,
                'i', Blocks.OBSERVER,
                'm', MachineCaseItem.INSTANCE);
    }
}
