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
package com.buuz135.industrial.tile.ore;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import net.ndrei.teslacorelib.inventory.LockableItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OreSieveTile extends CustomElectricMachine {

    private LockableItemHandler itemInput;
    private IFluidTank input;
    private ItemStackHandler output;

    public OreSieveTile() {
        super(OreSieveTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        input = this.addSimpleFluidTank(8000, "input", EnumDyeColor.BLUE, 60 + 10, 25, FluidTankType.INPUT, fluidStack -> true, fluidStack -> false);
        itemInput = (LockableItemHandler) this.addSimpleInventory(1, "input", EnumDyeColor.GREEN, "input", new BoundingRectangle(18 * 5 - 2 + 10, 26, 18, 18), (stack, integer) -> true, (stack, integer) -> false, true, null);
        output = (ItemStackHandler) this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "output", new BoundingRectangle(18 * 6 + 8 + 10, 26 + 18, 18, 18), (stack, integer) -> false, (stack, integer) -> true, false, null);
    }

    @Override
    protected float performWork() {
        for (OreFluidEntrySieve recipe : OreFluidEntrySieve.ORE_FLUID_SIEVE) {
            if (recipe.getInput().isFluidEqual(input.getFluid()) && input.drain(recipe.getInput().amount, false) != null &&
                    input.drain(recipe.getInput().amount, false).amount == recipe.getInput().amount && ItemHandlerHelper.insertItem(output, recipe.getOutput().copy(), true).isEmpty()
                    && recipe.getSieveItem().isItemEqual(itemInput.getStackInSlot(0))) {
                itemInput.getStackInSlot(0).shrink(1);
                input.drain(recipe.getInput().amount, true);
                ItemHandlerHelper.insertItem(output, recipe.getOutput().copy(), false);
                return 1;
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(@NotNull BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(84 + 10, 45, 25, 18, new ResourceLocation(Reference.MOD_ID, "textures/gui/jei.png"), 24, 5));
        return pieces;
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }
}
