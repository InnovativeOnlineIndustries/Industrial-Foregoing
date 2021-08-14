/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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

package com.buuz135.industrial.block.generator;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.generator.tile.MycelialReactorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.buuz135.industrial.worlddata.MycelialDataManager;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MycelialReactorBlock extends IndustrialBlock<MycelialReactorTile> {

    public MycelialReactorBlock() {
        super("mycelial_reactor", Properties.from(Blocks.IRON_BLOCK), MycelialReactorTile.class, ModuleGenerator.TAB_GENERATOR);
    }

    @Override
    public IFactory<MycelialReactorTile> getTileEntityFactory() {
        return MycelialReactorTile::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof MycelialReactorTile && placer != null){
            ((MycelialReactorTile) entity).setOwner(placer.getUniqueID().toString());
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (player.isSneaking() && !worldIn.isRemote && tileEntity instanceof MycelialReactorTile){
            List<String> available = MycelialDataManager.getReactorAvailable(((MycelialReactorTile) tileEntity).getOwner(), worldIn, false);
            if (available.size() != IMycelialGeneratorType.TYPES.size()){
                player.sendMessage(new StringTextComponent("Generators not running:").mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
            for (IMycelialGeneratorType type : IMycelialGeneratorType.TYPES) {
                if (!available.contains(type.getName())){
                    player.sendMessage(new TranslationTextComponent("block.industrialforegoing.mycelial_" + type.getName()).mergeStyle(TextFormatting.RED), player.getUniqueID());
                }
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }

}
