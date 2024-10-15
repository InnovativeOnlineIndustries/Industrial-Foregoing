package com.buuz135.industrial.block.transportstorage.transporter.filter;

import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemFilter extends RegulatorFilter<ItemStack, IItemHandler> {
    public ItemFilter(int locX, int locY, int sizeX, int sizeY, int smallMultiplier, int bigMultiplier, int maxAmount, String label) {
        super(locX, locY, sizeX, sizeY, smallMultiplier, bigMultiplier, maxAmount, label);
    }

    /**
     * Used to check if provided ItemStack matches the filter. This method ignores regulation rule.
     *
     * @param stack ItemStack to check
     * @param handler Capability of this filter.
     * @return True if provided TYPE matches, false otherwise.
     */
    @Override
    public boolean matches(ItemStack stack, IItemHandler handler) {
        if (isEmpty()) return !isWhitelisted();

        for (IFilter.GhostSlot slot : this.getFilterSlots())
            if (ItemStack.isSameItem(slot.getStack(), stack)) return isWhitelisted();

        return !isWhitelisted();
    }

    /**
     * Used to get the amount to extract of the provided ItemStack.
     *
     * @param stack ItemStack to check.
     * @param handler Capability of this filter.
     * @return Amount to extract, complying to the regulation rule.
     */
    @Override
    public int getExtractAmount(ItemStack stack, IItemHandler handler) {
        if (!matches(stack, handler)) return 0;
        if (isEmpty() || !isRegulated() || !isWhitelisted()) return stack.getCount();

        // Items - Minimum Left in storage (0 or above)
        return Math.max(0, getStorageAmount(stack, handler) - getFilterAmount(stack));
    }

    /**
     * Used to get the possible amount of ItemStack to insert.
     * @param stack ItemStack to check.
     * @param handler Capability of this filter.
     * @return Possible amount to insert, complying to the regulation rule.
     * @implNote This method does not check for available space in the provided IItemHandler.
     */
    @Override
    public int getInsertAmount(ItemStack stack, IItemHandler handler) {
        if (!matches(stack, handler)) return 0;
        if (isEmpty() || !isRegulated() || !isWhitelisted()) return stack.getCount();

        // Maximum amount in storage - items (0 or above)
        return Math.max(0, getFilterAmount(stack) - getStorageAmount(stack, handler));
    }

    /**
     * Used to get the amount of the item set in the filter. Checks all slots of the filter.
     * @param stack Item to look for.
     * @return Amount of the item.
     * @apiNote If not Regulated, will return either 0 or 1.
     */
    public int getFilterAmount(ItemStack stack) {
        int amount = 0;

        for (IFilter.GhostSlot slot : this.getFilterSlots()) {
            if (ItemStack.isSameItem(slot.getStack(), stack)) {
                if (!isRegulated()) return 1;
                amount += slot.getAmount();
            }
        }

        return amount;
    }

    /**
     * Used to get the amount of the item in the provided IItemHandler.
     * @param stack Item to look for.
     * @return Amount of the item.
     */
    public int getStorageAmount(ItemStack stack, @NotNull IItemHandler handler) {
        int amount = 0;

        for (int i = 0; i < handler.getSlots(); i++) {
            if (ItemStack.isSameItem(handler.getStackInSlot(i), stack))
                amount += handler.getStackInSlot(i).getCount();
        }

        return amount;
    }
}
