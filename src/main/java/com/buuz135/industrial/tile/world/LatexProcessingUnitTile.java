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
package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class LatexProcessingUnitTile extends CustomElectricMachine {

    private IFluidTank waterTank;
    private IFluidTank latexTank;
    private ItemStackHandler itemOut;


    public LatexProcessingUnitTile() {
        super(LatexProcessingUnitTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        waterTank = this.addFluidTank(FluidRegistry.WATER, 8000, EnumDyeColor.BLUE, "Water tank", new BoundingRectangle(17 * 2 + 12, 25, 18, 54));
        latexTank = this.addFluidTank(FluidsRegistry.LATEX, 8000, EnumDyeColor.GRAY, "Latex tank", new BoundingRectangle(17 * 3 + 16, 25, 18, 54));
        itemOut = new ItemStackHandler(3 * 3);
        this.addInventory(new ColoredItemHandler(itemOut, EnumDyeColor.ORANGE, "Output items", new BoundingRectangle(18 * 6 + 3, 25, 18 * 3, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                int i = 0;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
                        ++i;
                    }
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        3, 3,
                        BasicTeslaGuiContainer.Companion.getMACHINE_BACKGROUND(), 108, 225, EnumDyeColor.ORANGE));
                return pieces;
            }
        });
        this.addInventoryToStorage(itemOut, "latex_processing_unit_out");
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (waterTank.getFluid() != null && latexTank.getFluid() != null && waterTank.drain(1000, false).amount == 1000 && latexTank.drain(75, false).amount == 75 && ItemHandlerHelper.insertItem(itemOut, new ItemStack(ItemRegistry.tinyDryRubber, 1), true).isEmpty()) {
            waterTank.drain(1000, true);
            latexTank.drain(75, true);
            ItemHandlerHelper.insertItem(itemOut, new ItemStack(ItemRegistry.tinyDryRubber, 1), false);
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return super.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStack stack = fluidItems.getStackInSlot(0);
        if (!stack.isEmpty()) {
            FluidStack fluid = FluidUtil.getFluidContained(stack);
            if (fluid != null) {
                if (fluid.getFluid() == FluidRegistry.WATER) ItemStackUtils.fillTankFromItem(fluidItems, waterTank);
                if (fluid.getFluid() == FluidsRegistry.LATEX) ItemStackUtils.fillTankFromItem(fluidItems, latexTank);
            }
        }
    }
}
