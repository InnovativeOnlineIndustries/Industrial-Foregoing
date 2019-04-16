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
import com.buuz135.industrial.tile.agriculture.HydratorTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.items.MachineCaseItem;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class HydratorBlock extends CustomAreaOrientedBlock<HydratorTile> {

    public HydratorBlock() {
        super("hydrator", HydratorTile.class, Material.ROCK, 1000, 10, RangeType.UP, 4, 0, true);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pfp", "cmc", "rir",
                'p', ItemRegistry.plastic,
                'f', Items.WATER_BUCKET,
                'r', "gearIron",
                'm', MachineCaseItem.INSTANCE,
                'i', Blocks.PISTON,
                'c', ItemRegistry.fertilizer);
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.AGRICULTURE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        for (BlockPos pos1 : BlockPos.getAllInBoxMutable(pos.add(-2, 0, -2), pos.add(2, 0, 2))) {
            if (worldIn.getBlockState(pos1).getBlock() instanceof IGrowable && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean()) {
                worldIn.spawnParticle(EnumParticleTypes.WATER_WAKE, pos1.getX() + rand.nextDouble(), pos1.getY(), pos1.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity entity = worldIn.getTileEntity(pos);
            if (entity instanceof HydratorTile) {
                ((HydratorTile) entity).getFarmlandTicket().invalidate();
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
