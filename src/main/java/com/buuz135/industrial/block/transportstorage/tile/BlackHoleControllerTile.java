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
package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.block.transportstorage.BlackHoleTankBlock;
import com.buuz135.industrial.block.transportstorage.BlackHoleUnitBlock;
import com.buuz135.industrial.module.ModuleTransportStorage;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlackHoleControllerTile extends ActiveTile<BlackHoleControllerTile> {

    @Save
    private InventoryComponent<BlackHoleControllerTile> units_storage;

    private BlackHoleControllerInventory inventory;
    private BlackHoleControllerTank tank;
    private LazyOptional<BlackHoleControllerInventory> lazyInventory;
    private LazyOptional<BlackHoleControllerTank> lazyTank;

    public BlackHoleControllerTile(BlockPos blockPos, BlockState blockState) {
        super((BasicTileBlock<BlackHoleControllerTile>) ModuleTransportStorage.BLACK_HOLE_CONTROLLER.get(), blockPos, blockState);
        addInventory(this.units_storage = new InventoryComponent<BlackHoleControllerTile>("units_storage", 53, 20, 4*4)
                .setSlotLimit(1)
                .setInputFilter((itemStack, integer) -> itemStack.getItem() instanceof BlackHoleTankBlock.BlackHoleTankItem || itemStack.getItem() instanceof BlackHoleUnitBlock.BlackHoleUnitItem)
                .setOutputFilter((itemStack, integer) -> false)
                .setRange(4,4)
        );
        for (int i = 0; i < this.units_storage.getSlots(); i++) {
            this.units_storage.setSlotToColorRender(i, DyeColor.BLUE);
        }
        this.inventory = new BlackHoleControllerInventory();
        this.tank = new BlackHoleControllerTank();
        this.lazyInventory = LazyOptional.of(() -> this.inventory);
        this.lazyTank = LazyOptional.of(() -> this.tank);
    }

    @Nonnull
    @Override
    public BlackHoleControllerTile getSelf() {
        return this;
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return lazyInventory.cast();
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return lazyTank.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public InteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return InteractionResult.SUCCESS;
    }

    private class BlackHoleControllerInventory implements IItemHandler{

        @Override
        public int getSlots() {
            return 16;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack bl = units_storage.getStackInSlot(slot);
            if (!bl.isEmpty() && bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
                IItemHandler handler = bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                return handler.getStackInSlot(0);
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemStack bl = units_storage.getStackInSlot(slot);
            if (!bl.isEmpty() && bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
                IItemHandler handler = bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                return handler.insertItem(0, stack, simulate);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack bl = units_storage.getStackInSlot(slot);
            if (!bl.isEmpty() && bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
                IItemHandler handler = bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                return handler.extractItem(0, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            ItemStack bl = units_storage.getStackInSlot(slot);
            if (!bl.isEmpty() && bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
                IItemHandler handler = bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                return handler.getSlotLimit(0);
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            ItemStack bl = units_storage.getStackInSlot(slot);
            if (!bl.isEmpty() && bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()){
                IItemHandler handler = bl.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
                return handler.isItemValid(0, stack);
            }
            return false;
        }
    }

    private class BlackHoleControllerTank implements IFluidHandler {

        @Override
        public int getTanks() {
            return 16;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            ItemStack bl = units_storage.getStackInSlot(tank);
            if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                IFluidHandler handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                return handler.getFluidInTank(0);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            ItemStack bl = units_storage.getStackInSlot(tank);
            if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                IFluidHandler handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                return handler.getTankCapacity(0);
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            ItemStack bl = units_storage.getStackInSlot(tank);
            if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                IFluidHandler handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                return handler.isFluidValid(0, stack);
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            for (int i = 0; i < getTanks(); i++) {
                ItemStack bl = units_storage.getStackInSlot(i);
                if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                    IFluidHandler handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                    int amount = handler.fill(resource, action);
                    if (amount > 0) return amount;
                }
            }
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            for (int i = 0; i < getTanks(); i++) {
                ItemStack bl = units_storage.getStackInSlot(i);
                if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                    IFluidHandlerItem handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                    FluidStack fluid = handler.drain(resource, action);
                    if (!fluid.isEmpty()){
                        if (handler.getFluidInTank(0).isEmpty()){
                            units_storage.setStackInSlot(i, handler.getContainer());
                        }
                        return fluid;
                    }
                }
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            for (int i = 0; i < getTanks(); i++) {
                ItemStack bl = units_storage.getStackInSlot(i);
                if (!bl.isEmpty() && bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
                    IFluidHandlerItem handler = bl.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
                    FluidStack fluid = handler.drain(maxDrain, action);
                    if (!fluid.isEmpty()){
                        if (handler.getFluidInTank(0).isEmpty()){
                            units_storage.setStackInSlot(i, handler.getContainer());
                        }
                        return fluid;
                    }
                }
            }
            return FluidStack.EMPTY;
        }
    }
}
