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
package com.buuz135.industrial.gui.transporter;

import com.buuz135.industrial.block.transportstorage.tile.TransporterTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerTransporter extends Container {

    @ObjectHolder("industrialforegoing:transporter")
    public static ContainerType<ContainerTransporter> TYPE;

    private final TransporterTile conveyor;
    private Direction facing;

    public ContainerTransporter(int id, PlayerInventory player, PacketBuffer buffer) {
        this(id, (TransporterTile) player.player.getEntityWorld().getTileEntity(buffer.readBlockPos()), buffer.readEnumValue(Direction.class), player);
    }

    public ContainerTransporter(int id, TransporterTile conveyor, Direction facing, PlayerInventory player) {
        super(TYPE, id);
        this.conveyor = conveyor;
        this.facing = facing;
        if (!conveyor.hasUpgrade(facing) && conveyor.getTransporterTypeMap().size() > 0) {
            this.facing = conveyor.getTransporterTypeMap().keySet().iterator().next();
        }
        createPlayerInventory(player);
    }

    private void createPlayerInventory(PlayerInventory player) {
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
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public TransporterTile getConveyor() {
        return conveyor;
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
