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
package com.buuz135.industrial.tile.misc;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class BlackHoleControllerTileReworked extends CustomSidedTileEntity {

    private ItemStackHandler storage;
    private BlackHoleControllerItemHandler itemHandler = new BlackHoleControllerItemHandler(this);
    private BlackHoleControllerTankHandler tankHandler = new BlackHoleControllerTankHandler(this);

    public BlackHoleControllerTileReworked() {
        super(BlackHoleControllerTileReworked.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        storage = new ItemStackHandler(12) {
            @Override
            protected void onContentsChanged(int slot) {
                BlackHoleControllerTileReworked.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(storage, EnumDyeColor.YELLOW, "Black hole units", (int) (15 + 18 * 2.5), 22, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Item.getItemFromBlock(BlockRegistry.blackHoleUnitBlock)) || stack.getItem().equals(Item.getItemFromBlock(BlockRegistry.blackHoleTankBlock));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return super.canExtractItem(slot);
            }
        });
        this.addInventoryToStorage(storage, "storage");

    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    public ItemStackHandler getStorage() {
        return storage;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == null) return false;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) tankHandler;
        return super.getCapability(capability, facing);
    }

    public void dropItems() {
        for (int i = 0; i < storage.getSlots(); ++i) {
            ItemStack stack = storage.getStackInSlot(i);
            if (!stack.isEmpty()) {
                InventoryHelper.spawnItemStack(this.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    @Override
    protected void innerUpdate() {

    }

    private class BlackHoleControllerItemHandler implements IItemHandler {

        private BlackHoleControllerTileReworked tile;

        public BlackHoleControllerItemHandler(BlackHoleControllerTileReworked tile) {
            this.tile = tile;
        }

        @Override
        public int getSlots() {
            return 12;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if (tile.getStorage().getStackInSlot(slot).isEmpty()) {
                return ItemStack.EMPTY;
            } else if (tile.getStorage().getStackInSlot(slot).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                return tile.getStorage().getStackInSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (tile.getStorage().getStackInSlot(slot).isEmpty()) {
                return stack;
            } else if (tile.getStorage().getStackInSlot(slot).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) &&
                    !tile.getStorage().getStackInSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(0, stack, true).equals(stack)) {
                return tile.getStorage().getStackInSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(0, stack, simulate);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (tile.getStorage().getStackInSlot(slot).isEmpty()) {
                return ItemStack.EMPTY;
            } else if (tile.getStorage().getStackInSlot(slot).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                return tile.getStorage().getStackInSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).extractItem(0, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }
    }

    private class BlackHoleControllerTankHandler implements IFluidHandler {

        private final BlackHoleControllerTileReworked tileReworked;

        private BlackHoleControllerTankHandler(BlackHoleControllerTileReworked tileReworked) {
            this.tileReworked = tileReworked;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            List<IFluidTankProperties> propertiesList = new ArrayList<>();
            for (int i = 0; i < storage.getSlots(); i++) {
                if (storage.getStackInSlot(i).isEmpty()) continue;
                if (!storage.getStackInSlot(i).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                    continue;
                for (IFluidTankProperties tankProperty : storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()) {
                    propertiesList.add(tankProperty);
                }
            }
            return propertiesList.toArray(new IFluidTankProperties[propertiesList.size()]);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            for (int i = 0; i < storage.getSlots(); i++) {
                if (storage.getStackInSlot(i).isEmpty()) continue;
                if (!storage.getStackInSlot(i).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                    continue;
                if (storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).fill(resource, false) == 0)
                    continue;
                return storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).fill(resource, doFill);
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            for (int i = 0; i < storage.getSlots(); i++) {
                if (storage.getStackInSlot(i).isEmpty()) continue;
                if (!storage.getStackInSlot(i).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                    continue;
                if (storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(resource, false) == null)
                    continue;
                return storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(resource, doDrain);
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            for (int i = 0; i < storage.getSlots(); i++) {
                if (storage.getStackInSlot(i).isEmpty()) continue;
                if (!storage.getStackInSlot(i).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                    continue;
                if (storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(maxDrain, false) == null)
                    continue;
                return storage.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).drain(maxDrain, doDrain);
            }
            return null;
        }
    }

}
