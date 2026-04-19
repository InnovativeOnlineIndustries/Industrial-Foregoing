/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2026, Buuz135
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
package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.item.IFCustomItem;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AddonItem extends IFCustomItem {
    public AddonItem(String name, TitaniumTab tab, Properties builder) {
        super(name, tab, builder);
    }

    public AddonItem(String name, TitaniumTab tab) {
        super(name, tab);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            var blockpos = context.getClickedPos();
            var entity = context.getLevel().getBlockEntity(blockpos);
            if (entity instanceof MachineTile<?> machineTile) {
                var stack = ItemHandlerHelper.copyStackWithSize(context.getItemInHand(), 1);
                if (machineTile.canAcceptAugment(stack)) {
                    var augmentInv = machineTile.getAugmentInventory();
                    for (int i = 0; i < augmentInv.getSlots(); i++) {
                        if (augmentInv.getStackInSlot(i).isEmpty()) {
                            augmentInv.setStackInSlot(i, stack);
                            context.getItemInHand().shrink(1);
                            return InteractionResult.CONSUME_PARTIAL;
                        }
                    }
                }
            }
        }
        return super.useOn(context);
    }
}
