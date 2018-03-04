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
