package com.buuz135.industrial.block.transportstorage.transporter.filter;

import com.buuz135.industrial.proxy.block.filter.IFilter;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidFilter extends RegulatorFilter<FluidStack, IFluidHandler> {
    public FluidFilter(int locX, int locY, int sizeX, int sizeY, int smallMultiplier, int bigMultiplier, int maxAmount, String label) {
        super(locX, locY, sizeX, sizeY, smallMultiplier, bigMultiplier, maxAmount, label);
    }

    /**
     * Used to check if provided FluidStack matches the filter. This method ignores regulation rule.
     *
     * @param stack         FluidStack to check
     * @param handler Capability of this filter.
     * @return True if provided FluidStack matches, false otherwise.
     */
    @Override
    public boolean matches(FluidStack stack, IFluidHandler handler) {
        if (isEmpty()) return !isWhitelisted();

        for (IFilter.GhostSlot slot : this.getFilterSlots()) {
            FluidStack original = FluidUtil.getFluidContained(slot.getStack()).orElse(null);
            if (original != null && FluidStack.isSameFluidSameComponents(original, stack)) return isWhitelisted();
        }

        return !isWhitelisted();
    }

    /**
     * Used to get the amount to extract of the provided FluidStack.
     *
     * @param stack         FluidStack to check.
     * @param handler Capability of this filter.
     * @return Amount to extract, complying to the regulation rule.
     */
    @Override
    public int getExtractAmount(FluidStack stack, IFluidHandler handler) {
        if (!matches(stack, handler)) return 0;
        if (isEmpty() || !isRegulated() || !isWhitelisted()) return stack.getAmount();

        // Fluid - Minimum Left in storage (0 or above)
        return Math.max(0, getStorageAmount(stack, handler) - getFilterAmount(stack));
    }

    /**
     * Used to get the possible amount of FluidStack to insert.
     *
     * @param stack         FluidStack to check.
     * @param handler Capability of this filter.
     * @return Possible amount to insert, complying to the regulation rule.
     */
    @Override
    public int getInsertAmount(FluidStack stack, IFluidHandler handler) {
        if (!matches(stack, handler)) return 0;
        if (isEmpty() || !isRegulated() || !isWhitelisted()) return stack.getAmount();

        // Maximum amount in storage - items (0 or above)
        return Math.max(0, getFilterAmount(stack) - getStorageAmount(stack, handler));
    }

    /**
     * Used to get the amount of the item set in the filter. Checks all slots of the filter.
     * @param stack Fluid to look for.
     * @return Amount of the item.
     * @apiNote If not Regulated, will return either 0 or 1.
     */
    public int getFilterAmount(FluidStack stack) {
        int amount = 0;

        for (IFilter.GhostSlot slot : this.getFilterSlots()) {
            FluidStack original = FluidUtil.getFluidContained(slot.getStack()).orElse(null);
            if (original != null && FluidStack.isSameFluidSameComponents(original, stack)) {
                if (!isRegulated()) return 1;
                amount += slot.getAmount();
            }
        }

        return amount;
    }

    /**
     * Used to get the amount of the item in the provided IFluidHandler.
     * @param stack Fluid to look for.
     * @return Amount of the item.
     */
    public int getStorageAmount(FluidStack stack, @NotNull IFluidHandler handler) {
        int amount = 0;

        for (int i = 0; i < handler.getTanks(); i++) {
            if (handler.isFluidValid(i, stack) && FluidStack.isSameFluidSameComponents(handler.getFluidInTank(i), stack)) {
                amount += handler.getFluidInTank(i).getAmount();
            }
        }

        return amount;
    }
}
