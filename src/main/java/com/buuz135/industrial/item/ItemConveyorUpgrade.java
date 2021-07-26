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
import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class ItemConveyorUpgrade extends IFCustomItem {

    private final ConveyorUpgradeFactory factory;

    public ItemConveyorUpgrade(ConveyorUpgradeFactory upgradeFactory, CreativeModeTab group) {
        super("conveyor_" + upgradeFactory.getRegistryName().getPath() + "_upgrade", group);
        this.factory = upgradeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getPlayer().isCrouching()) {
            BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
            if (tile instanceof ConveyorTile && ((ConveyorTile) tile).getConveyorType().isVertical())
                return InteractionResult.PASS;
            if (tile instanceof IBlockContainer) {
                Direction side = factory.getSideForPlacement(context.getLevel(), context.getClickedPos(), context.getPlayer());
                if (!((IBlockContainer) tile).hasUpgrade(side)) {
                    ((IBlockContainer) tile).addUpgrade(side, factory);
                    if (!context.getPlayer().isCreative()) context.getItemInHand().shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (factory == null)
            return "conveyor.upgrade.error";
        return String.format("conveyor.upgrade.%s.%s", factory.getRegistryName().getNamespace(), factory.getRegistryName().getPath());
    }


    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {

    }
}
