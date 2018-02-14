package com.buuz135.industrial.tile.api;

import com.buuz135.industrial.item.addon.movility.TransferAddon;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public interface IAcceptsTransferAddons {

    boolean canAcceptAddon(TransferAddon transferAddon);

    default void workTransferAddon(TileEntity tileEntity, List<ItemStack> addons) {
        for (ItemStack item : addons) {
            if (item.getItem() instanceof TransferAddon) {
                TransferAddon addon = (TransferAddon) item.getItem();
                if (addon.actionTransfer(tileEntity.getWorld(), tileEntity.getPos(), addon.getFacingFromMeta(item), addon.getTransferAmount(item)))
                    break;
            }
        }
    }
}
