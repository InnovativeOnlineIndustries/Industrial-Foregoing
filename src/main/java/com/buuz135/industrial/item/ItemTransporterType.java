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

package com.buuz135.industrial.item;

import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.transporter.TransporterTypeFactory;
import com.buuz135.industrial.module.ModuleTransportStorage;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class ItemTransporterType extends IFCustomItem {

    public TransporterTypeFactory factory;

    public ItemTransporterType(TransporterTypeFactory transporterTypeFactory, ItemGroup group) {
        super(transporterTypeFactory.getRegistryName().getPath() + "_transporter_type", group);
        this.factory = transporterTypeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos().offset(context.getFace());
        Direction side = context.getFace().getOpposite();
        if (factory.canBeAttachedAgainst(context.getWorld(), context.getPos(), side.getOpposite())) {
            if (!context.getWorld().getBlockState(context.getPos().offset(context.getFace())).isIn(ModuleTransportStorage.TRANSPORTER) && context.getWorld().getBlockState(context.getPos().offset(context.getFace())).isAir()) {
                context.getWorld().setBlockState(context.getPos().offset(context.getFace()), ModuleTransportStorage.TRANSPORTER.getDefaultState());
                pos = context.getPos().offset(context.getFace());
            }
            TileEntity tile = context.getWorld().getTileEntity(pos);
            if (tile instanceof IBlockContainer) {
                if (!((IBlockContainer) tile).hasUpgrade(side) && factory.canBeAttachedAgainst(context.getWorld(), context.getPos(), side.getOpposite())) {
                    ((IBlockContainer) tile).addUpgrade(side, factory);
                    if (!context.getPlayer().isCreative()) context.getItem().shrink(1);
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }


    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }
}
