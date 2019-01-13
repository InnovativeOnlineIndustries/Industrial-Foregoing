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
package com.buuz135.industrial.tile.api;

import com.buuz135.industrial.item.addon.movility.TransferAddon;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public interface IAcceptsTransferAddons {

    boolean canAcceptAddon(TransferAddon transferAddon);

    default void workTransferAddon(TileEntity tileEntity, ItemStackHandler addons) {
        for (int i = 0; i < addons.getSlots(); i++) {
            ItemStack item = addons.getStackInSlot(i);
            if (!item.isEmpty() && item.getItem() instanceof TransferAddon) {
                TransferAddon addon = (TransferAddon) item.getItem();
                if (addon.actionTransfer(tileEntity.getWorld(), tileEntity.getPos(), addon.getFacingFromMeta(item), addon.getTransferAmount(item)))
                    break;
            }
        }
    }
}
