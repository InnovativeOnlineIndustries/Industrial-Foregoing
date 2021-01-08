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
                    //return itemstack;
                }
            }
        }
        return super.transferStackInSlot(player, index);
    }
}
