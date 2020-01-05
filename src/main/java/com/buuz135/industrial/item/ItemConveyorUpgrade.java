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
package com.buuz135.industrial.item;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.api.conveyor.IConveyorContainer;
import com.buuz135.industrial.block.transport.tile.ConveyorTile;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;

import java.util.function.Consumer;

public class ItemConveyorUpgrade extends IFCustomItem {

    private final ConveyorUpgradeFactory factory;

    public ItemConveyorUpgrade(ConveyorUpgradeFactory upgradeFactory, ItemGroup group) {
        super("conveyor_" + upgradeFactory.getRegistryName().getPath() + "_upgrade", group);
        this.factory = upgradeFactory;
        this.factory.setUpgradeItem(this);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getPlayer().isCrouching()) {
            TileEntity tile = context.getWorld().getTileEntity(context.getPos());
            if (tile instanceof ConveyorTile && ((ConveyorTile) tile).getConveyorType().isVertical())
                return ActionResultType.PASS;
            if (tile instanceof IConveyorContainer) {
                Direction side = factory.getSideForPlacement(context.getWorld(), context.getPos(), context.getPlayer());
                if (!((IConveyorContainer) tile).hasUpgrade(side)) {
                    ((IConveyorContainer) tile).addUpgrade(side, factory);
                    if (!context.getPlayer().isCreative()) context.getItem().shrink(1);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (factory == null)
            return "conveyor.upgrade.error";
        return String.format("conveyor.upgrade.%s.%s", factory.getRegistryName().getNamespace(), factory.getRegistryName().getPath());
    }


    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {

    }
}
