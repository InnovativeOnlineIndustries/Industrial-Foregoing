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

package com.buuz135.industrial.container;

import com.buuz135.industrial.item.infinity.item.ItemInfinityBackpack;
import com.buuz135.industrial.worlddata.BackpackDataManager;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class BackpackContainer extends BasicAddonContainer {

    private final String id;
    private LocatorInstance instance;

    public BackpackContainer(Object provider, LocatorInstance locatorInstance, IWorldPosCallable worldPosCallable, PlayerInventory playerInventory, int containerId, String id) {
        super(provider, locatorInstance, worldPosCallable, playerInventory, containerId);
        this.id = id;
        this.instance = locatorInstance;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            BackpackDataManager.BackpackItemHandler handler = BackpackDataManager.getData(player.world).getBackpack(id);
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            for (int pos = 0; pos < handler.getSlots(); pos++) {
                if (!handler.getStackInSlot(pos).isEmpty() && handler.isItemValid(pos, itemstack1)){
                    ItemStack result = handler.insertItem(pos, itemstack1, false);
                    slot.putStack(result);
                    if (player instanceof ServerPlayerEntity) ItemInfinityBackpack.sync(player.world, id , (ServerPlayerEntity) player);
                    detectAndSendChanges();
                    break;
                    //return itemstack;
                }
            }
        }
        return super.transferStackInSlot(player, index);
    }
}
