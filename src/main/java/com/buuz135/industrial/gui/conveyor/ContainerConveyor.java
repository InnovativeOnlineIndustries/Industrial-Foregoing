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

package com.buuz135.industrial.gui.conveyor;

import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ContainerConveyor extends AbstractContainerMenu {

    public static DeferredHolder<MenuType<?>, MenuType<?>> TYPE;

    private final ConveyorTile conveyor;
    private Direction facing;

    public ContainerConveyor(int id, Inventory player, FriendlyByteBuf buffer) {
        this(id, (ConveyorTile) player.player.getCommandSenderWorld().getBlockEntity(buffer.readBlockPos()), buffer.readEnum(Direction.class), player);
    }

    public ContainerConveyor(int id, ConveyorTile conveyor, Direction facing, Inventory player) {
        super(TYPE.get(), id);
        this.conveyor = conveyor;
        this.facing = facing;
        if (!conveyor.hasUpgrade(facing) && conveyor.getUpgradeMap().size() > 0) {
            this.facing = conveyor.getUpgradeMap().keySet().iterator().next();
        }
        createPlayerInventory(player);
    }

    private void createPlayerInventory(Inventory player) {
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(player, k, 8 + k * 18, 142));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    public ConveyorTile getConveyor() {
        return conveyor;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
