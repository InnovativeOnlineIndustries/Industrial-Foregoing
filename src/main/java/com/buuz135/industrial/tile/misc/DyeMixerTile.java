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

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.client.infopiece.DyeTankInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;

import java.util.ArrayList;
import java.util.List;

public class DyeMixerTile extends CustomElectricMachine {

    private static ColorUsage[] colorUsages = {new ColorUsage(1, 1, 1), //0
            new ColorUsage(1, 1, 1),//1
            new ColorUsage(1, 0, 1),//2
            new ColorUsage(0, 0, 1),//3
            new ColorUsage(0, 1, 1),//4
            new ColorUsage(0, 1, 0),//5
            new ColorUsage(1, 0, 0),//6
            new ColorUsage(1, 1, 1),//7
            new ColorUsage(1, 1, 1),//8
            new ColorUsage(0, 0, 1),//9
            new ColorUsage(1, 0, 1),//10
            new ColorUsage(0, 0, 3),//11
            new ColorUsage(1, 1, 1),//12
            new ColorUsage(0, 3, 0),//13
            new ColorUsage(3, 0, 0),//14
            new ColorUsage(1, 1, 1)};//15
    private ItemStackHandler inputDyes;
    private ItemStackHandler lens;
    private ItemStackHandler output;
    private int r;
    private int g;
    private int b;

    public DyeMixerTile() {
        super(DyeMixerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inputDyes = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                DyeMixerTile.this.markDirty();
            }
        };
        this.addInventory(new ColoredItemHandler(inputDyes, EnumDyeColor.RED, "Dye items", new BoundingRectangle(18 * 2 + 10, 25, 23 * 2 + 18, 18 * 3)) {

            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ItemStackUtils.isStackOreDict(stack, new String[]{"dyeRed", "dyeGreen", "dyeBlue"}[slot]);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = new ArrayList<>();
                BoundingRectangle box = this.getBoundingBox();
                int i = 0;
                for (int x = 0; x < 3; x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 23, box.getTop() + 1));
                    ++i;
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = new ArrayList<>();
                BoundingRectangle box = this.getBoundingBox();
                for (int x = 0; x < 3; ++x) {
                    pieces.add(new TiledRenderedGuiPiece(box.getLeft() + 23 * x, box.getTop(), 18, 18, 1, 1, BasicTeslaGuiContainer.Companion.getMACHINE_BACKGROUND(), 108, 225, new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.GREEN, EnumDyeColor.BLUE}[x]));
                }
                return pieces;
            }
        });
        this.addInventoryToStorage(inputDyes, "inputDyes");
        lens = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                DyeMixerTile.this.markDirty();
            }
        };
        ;
        this.addInventory(new CustomColoredItemHandler(lens, EnumDyeColor.YELLOW, "Lens items", 18 * 7 + 6, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(ItemRegistry.laserLensItem);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(lens, "lens");
        output = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                DyeMixerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(output, EnumDyeColor.ORANGE, "Output items", 18 * 6 + 6, 25 + 18 * 2, 3, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(output, "output");
        registerSyncIntPart("red", nbtTagInt -> r = nbtTagInt.getInt(), () -> new NBTTagInt(r), SyncProviderLevel.GUI);
        registerSyncIntPart("green", nbtTagInt -> g = nbtTagInt.getInt(), () -> new NBTTagInt(g), SyncProviderLevel.GUI);
        registerSyncIntPart("blue", nbtTagInt -> b = nbtTagInt.getInt(), () -> new NBTTagInt(b), SyncProviderLevel.GUI);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> containerPieces = super.getGuiContainerPieces(container);
        for (int x = 0; x < 3; ++x) {
            containerPieces.add(new DyeTankInfoPiece(this, new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.GREEN, EnumDyeColor.BLUE}[x], 18 * 2 + 10 + 23 * x, 25 + 21, 125 + 18 * x, 72));
        }
        return containerPieces;
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        int[] values = {r, g, b};
        for (int i = 0; i < 3; ++i) {
            ItemStack stack = inputDyes.getStackInSlot(i);
            if (!stack.isEmpty() && values[i] + 3 <= 300) {
                stack.setCount(stack.getCount() - 1);
                values[i] = values[i] + 3;
            }
        }
        r = values[0];
        g = values[1];
        b = values[2];
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        ItemStack stack = lens.getStackInSlot(0);
        if (!stack.isEmpty()) {
            ColorUsage usage = colorUsages[stack.getMetadata()];
            if (r >= usage.getR() && g >= usage.getG() && b >= usage.getB()) {
                ItemStack out = new ItemStack(ItemRegistry.artificalDye, 1, stack.getMetadata());
                if (ItemHandlerHelper.insertItem(output, out, true).isEmpty()) {
                    r -= usage.getR();
                    g -= usage.getG();
                    b -= usage.getB();
                    ItemHandlerHelper.insertItem(output, out, false);
                    partialSync("red", true);
                    partialSync("green", true);
                    partialSync("blue", true);
                    return 1;
                }
            }
        }

        return 0;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound c = super.writeToNBT(compound);
        c.setInteger("R", r);
        c.setInteger("G", g);
        c.setInteger("B", b);
        return c;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("R")) r = compound.getInteger("R");
        if (compound.hasKey("G")) g = compound.getInteger("G");
        if (compound.hasKey("B")) b = compound.getInteger("B");
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    private static class ColorUsage {

        private int r;
        private int g;
        private int b;

        public ColorUsage(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }
    }
}
