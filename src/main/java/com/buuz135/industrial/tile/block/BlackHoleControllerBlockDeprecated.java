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
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleControllerTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlackHoleControllerBlockDeprecated extends CustomOrientedBlock<BlackHoleControllerTile> {

    public BlackHoleControllerBlockDeprecated() {
        super("black_hole_controller", BlackHoleControllerTile.class, Material.ROCK, 0, 0);
    }

    public void createRecipe() {
        RecipeUtils.addShapelessRecipe("black_hole_controller_deprecated", new ItemStack(BlockRegistry.blackHoleControllerBlock), new ItemStack(this));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity t = worldIn.getTileEntity(pos);
        if (t instanceof BlackHoleControllerTile) {
            ((BlackHoleControllerTile) t).dropItems();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

}
