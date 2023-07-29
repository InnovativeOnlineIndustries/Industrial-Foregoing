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
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class ItemTransporterType extends IFCustomItem {

    public TransporterTypeFactory factory;

    public ItemTransporterType(TransporterTypeFactory transporterTypeFactory, TitaniumTab group) {
        super(transporterTypeFactory.getName() + "_transporter_type", group);
        this.factory = transporterTypeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Direction side = context.getClickedFace().getOpposite();
        if (factory.canBeAttachedAgainst(context.getLevel(), context.getClickedPos(), side.getOpposite())) {
            if (!context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace())).is(ModuleTransportStorage.TRANSPORTER.getLeft().get()) && context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace())).isAir()) {
                context.getLevel().setBlockAndUpdate(context.getClickedPos().relative(context.getClickedFace()), ModuleTransportStorage.TRANSPORTER.getLeft().get().defaultBlockState());
                pos = context.getClickedPos().relative(context.getClickedFace());
            }
            BlockEntity tile = context.getLevel().getBlockEntity(pos);
            if (tile instanceof IBlockContainer) {
                if (!((IBlockContainer) tile).hasUpgrade(side) && factory.canBeAttachedAgainst(context.getLevel(), context.getClickedPos(), side.getOpposite())) {
                    ((IBlockContainer) tile).addUpgrade(side, factory);
                    if (!context.getPlayer().isCreative()) context.getItemInHand().shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }


    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {

    }
}
