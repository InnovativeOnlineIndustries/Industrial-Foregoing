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

import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.LockedInventoryTogglePiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FluidCrafterTile extends CustomSidedTileEntity {

    private IFluidTank tank;
    private LockableItemHandler crafting;
    private ItemStackHandler output;
    private int tick;

    public FluidCrafterTile() {
        super(FluidCrafterTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(8000, "tank", EnumDyeColor.BLUE, 18, 25, FluidTankType.BOTH, fluidStack -> true, fluidStack -> true);
        crafting = (LockableItemHandler) this.addSimpleInventory(9, "crafting", EnumDyeColor.GREEN, "crafting", new BoundingRectangle(58, 25, 18 * 3, 18 * 3), (stack, integer) -> true, (stack, integer) -> false, true, null);
        output = (ItemStackHandler) this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "output", new BoundingRectangle(58 + 18 * 5, 25 + 18, 18, 18), (stack, integer) -> false, (stack, integer) -> true, false, null);
        tick = 0;
    }

    @Override
    protected void innerUpdate() {
        if (this.world.isRemote) return;
        ++tick;
        if (tick >= 40) tick = 0;
        if (crafting.getLocked() && tick == 0 && hasOnlyOneFluid()) {
            Fluid fluid = getRecipeFluid();
            if (fluid == null) return;
            int bucketAmount = getFluidAmount(fluid);
            FluidStack stack = tank.drain(bucketAmount * 1000, false);
            if (stack != null && stack.getFluid().equals(fluid) && stack.amount == bucketAmount * 1000) {
                IRecipe recipe = CraftingUtils.findRecipe(world, simulateRecipeEntries(fluid));
                if (recipe == null || recipe.getRecipeOutput().isEmpty()) return;
                if (ItemHandlerHelper.insertItem(this.output, recipe.getRecipeOutput().copy(), true).isEmpty() && areAllSolidsPresent(fluid)) {
                    NonNullList<ItemStack> remaining = recipe.getRemainingItems(CraftingUtils.genCraftingInventory(world, simulateRecipeEntries(fluid)));
                    for (int i = 0; i < crafting.getSlots(); ++i) {
                        if (isStackCurrentFluid(fluid, crafting.getFilterStack(i))) continue;
                        if (remaining.get(i).isEmpty()) crafting.getStackInSlot(i).shrink(1);
                        else crafting.setStackInSlot(i, remaining.get(i).copy());
                    }
                    tank.drain(bucketAmount * 1000, true);
                    ItemHandlerHelper.insertItem(this.output, recipe.getRecipeOutput().copy(), false);
                }
            }
        }
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        tick = compound.getInteger("Tick");
        super.readFromNBT(compound);
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        NBTTagCompound compound1 = super.writeToNBT(compound);
        compound1.setInteger("Tick", tick);
        return compound1;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(118, 25 + 20, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        pieces.add(new LockedInventoryTogglePiece(18 * 6 + 8, 25 + 39, this, EnumDyeColor.GREEN));
        return pieces;
    }

    public Fluid getRecipeFluid() {
        if (tank.getFluid() != null) {
            return tank.getFluid().getFluid();
        }
        for (ItemStack stack : crafting.getFilter()) {
            if (stack.isEmpty()) continue;
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (fluidHandlerItem != null && fluidHandlerItem.drain(Integer.MAX_VALUE, false) != null)
                    return fluidHandlerItem.drain(Integer.MAX_VALUE, false).getFluid();
            }
        }
        return null;
    }

    public int getFluidAmount(Fluid fluid) {
        int i = 0;
        for (ItemStack stack : crafting.getFilter()) {
            if (stack.isEmpty()) continue;
            if (isStackCurrentFluid(fluid, stack)) ++i;
        }
        return i;
    }

    public boolean areAllSolidsPresent(Fluid fluid) {
        for (int i = 0; i < crafting.getSlots(); ++i) {
            if (isStackCurrentFluid(fluid, crafting.getFilter()[i]) || crafting.getFilter()[i].isEmpty())
                continue;
            if (crafting.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    private boolean isStackCurrentFluid(Fluid fluid, ItemStack stack) {
        if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) return false;
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        return fluidHandlerItem != null && fluidHandlerItem.drain(Integer.MAX_VALUE, false) != null && fluidHandlerItem.drain(Integer.MAX_VALUE, false).getFluid().equals(fluid);
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    public boolean hasOnlyOneFluid() {
        Fluid tankFluid = tank.getFluid() != null ? tank.getFluid().getFluid() : null;
        List<Fluid> fluids = new ArrayList<>();
        for (ItemStack stack : crafting.getFilter()) {
            if (stack.isEmpty()) continue;
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                FluidStack fluid = fluidHandlerItem.drain(Integer.MAX_VALUE, false);
                if (fluid != null && !fluids.contains(fluid.getFluid())) fluids.add(fluid.getFluid());
            }
        }
        if (tankFluid != null) fluids.remove(tankFluid);
        return fluids.size() <= 1;
    }

    public ItemStack[] simulateRecipeEntries(Fluid fluid) {
        ItemStack[] itemStacks = new ItemStack[9];
        for (int i = 0; i < crafting.getSlots(); ++i) {
            if (crafting.getFilter()[i].isEmpty()) {
                itemStacks[i] = ItemStack.EMPTY;
            } else if (isStackCurrentFluid(fluid, crafting.getFilterStack(i))) {
                itemStacks[i] = crafting.getFilter()[i].copy();
            } else {
                itemStacks[i] = crafting.getStackInSlot(i);
            }
        }
        return itemStacks;
    }

}
