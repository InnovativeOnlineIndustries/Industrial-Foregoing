package com.buuz135.industrial.gui.conveyor;

import com.buuz135.industrial.proxy.block.TileEntityConveyor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ContainerConveyor extends Container {

    private final TileEntityConveyor conveyor;
    private EnumFacing facing;

    public ContainerConveyor(TileEntityConveyor conveyor, EnumFacing facing, InventoryPlayer player) {
        this.conveyor = conveyor;
        this.facing = facing;
        if (!conveyor.hasUpgrade(facing) && conveyor.getUpgradeMap().size() > 0) {
            this.facing = conveyor.getUpgradeMap().keySet().iterator().next();
        }
        createPlayerInventory(player);
    }

    private void createPlayerInventory(InventoryPlayer player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(player, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public TileEntityConveyor getConveyor() {
        return conveyor;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
