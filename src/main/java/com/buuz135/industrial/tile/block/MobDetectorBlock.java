/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
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

public class MobDetectorBlock extends CustomAreaOrientedBlock<MobDetectorTile> {

    public MobDetectorBlock() {
        super("mob_detector", MobDetectorTile.class, Material.ROCK, 100, 10, RangeType.FRONT, 5, 1, true);
    }


    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof MobDetectorTile) {
            MobDetectorTile tile = (MobDetectorTile) world.getTileEntity(pos);
            return side == null || side.equals(tile.getFacing().getOpposite());
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

    @Override
    public BookCategory getCategory() {
        return BookCategory.MOB;
    }

}
