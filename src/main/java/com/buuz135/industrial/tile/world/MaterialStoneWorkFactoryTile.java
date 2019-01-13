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

import com.buuz135.industrial.proxy.client.infopiece.MaterialStoneWorkInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.CraftingUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import lombok.Getter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;

import java.util.*;

public class MaterialStoneWorkFactoryTile extends CustomElectricMachine {

    private static final String NBT_MODE = "Mode";

    @Getter
    private LinkedHashMap<ItemStackHandler, Mode> modeList;


    public MaterialStoneWorkFactoryTile() {
        super(MaterialStoneWorkFactoryTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        modeList = new LinkedHashMap<>();
        EnumDyeColor[] colors = new EnumDyeColor[]{EnumDyeColor.YELLOW, EnumDyeColor.BLUE, EnumDyeColor.GREEN, EnumDyeColor.ORANGE, EnumDyeColor.PURPLE};
        for (int i = 0; i < 5; ++i) {
            ItemStackHandler item = new ItemStackHandler(2);
            this.addInventory(new CustomColoredItemHandler(item, colors[i], "Material process", 50 + 24 * i, 25, 1, 2) {
                @Override
                public boolean canInsertItem(int slot, ItemStack stack) {
                    return false;
                }
            });
            this.addInventoryToStorage(item, "item" + i);
            modeList.put(item, Mode.NONE);
        }
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        for (int i = 0; i < modeList.size() - 1; ++i)
            pieces.add(new MaterialStoneWorkInfoPiece(this, 62 + 24 * i, 64, i));
        return pieces;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound compound1 = super.writeToNBT(compound);
        NBTTagCompound m = new NBTTagCompound();
        modeList.forEach((handler, mode) -> m.setString(String.valueOf(Iterators.indexOf(modeList.entrySet().iterator(), input -> input.getKey().equals(handler))), mode.toString()));
        compound1.setTag(NBT_MODE, m);
        return compound1;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(NBT_MODE)) {
            compound.getCompoundTag(NBT_MODE).getKeySet().forEach(s -> {
                Map.Entry<ItemStackHandler, Mode> it = Iterators.get(modeList.entrySet().iterator(), Integer.parseInt(s));
                modeList.replace(it.getKey(), Mode.valueOf(compound.getCompoundTag(NBT_MODE).getString(s)));
            });
        }
    }

    @Override
    protected float performWork() {
        int i = 0;
        ListIterator<Map.Entry<ItemStackHandler, Mode>> it = ImmutableList.copyOf(modeList.entrySet()).reverse().listIterator();
        int work = (int) Math.pow(2, speedUpgradeLevel());
        Map.Entry<ItemStackHandler, Mode> nextEntry = null;
        while (it.hasNext()) {
            Map.Entry<ItemStackHandler, Mode> currentEntry = it.next();
            ItemStack cobble = new ItemStack(Blocks.COBBLESTONE, work);
            if (nextEntry != null) {
                if (currentEntry.getValue() == Mode.FURNACE) {
                    for (int slot = 0; slot < currentEntry.getKey().getSlots(); ++slot) {
                        if (currentEntry.getKey().getStackInSlot(slot).isEmpty()) continue;
                        ItemStack stack = currentEntry.getKey().getStackInSlot(slot).copy();
                        stack.setCount(1);
                        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                        result.setCount(work);
                        if (!result.isEmpty() && ItemHandlerHelper.insertItem(nextEntry.getKey(), result, true).isEmpty()) {
                            ItemHandlerHelper.insertItem(nextEntry.getKey(), result, false);
                            currentEntry.getKey().getStackInSlot(slot).shrink(work);
                            return 1;
                        }
                    }
                }
                if (currentEntry.getValue() == Mode.CRAFT_BIG || currentEntry.getValue() == Mode.CRAFT_SMALL) {
                    int size = currentEntry.getValue() == Mode.CRAFT_BIG ? 3 : 2;
                    for (int slot = 0; slot < currentEntry.getKey().getSlots(); ++slot) {
                        if (currentEntry.getKey().getStackInSlot(slot).getCount() >= size * size) {
                            ItemStack result = CraftingUtils.findOutput(size, currentEntry.getKey().getStackInSlot(slot), world);
                            if (!result.isEmpty() && ItemHandlerHelper.insertItem(nextEntry.getKey(), result, true).isEmpty()) {
                                ItemHandlerHelper.insertItem(nextEntry.getKey(), result, false);
                                currentEntry.getKey().getStackInSlot(slot).shrink(size * size);
                                return 1;
                            }
                        }
                    }
                }
                if (currentEntry.getValue() == Mode.GRIND) {
                    for (int slot = 0; slot < currentEntry.getKey().getSlots(); ++slot) {
                        ItemStack result = CraftingUtils.getCrushOutput(new ItemStack(currentEntry.getKey().getStackInSlot(slot).getItem(), 1)).copy();
                        result.setCount(work);
                        if (!result.isEmpty() && ItemHandlerHelper.insertItem(nextEntry.getKey(), result, true).isEmpty()) {
                            ItemHandlerHelper.insertItem(nextEntry.getKey(), result, false);
                            currentEntry.getKey().getStackInSlot(slot).shrink(work);
                            return 1;
                        }
                    }
                }
                if (!it.hasNext()) {
                    if (!cobble.equals(ItemHandlerHelper.insertItem(currentEntry.getKey(), cobble, true))) {
                        ItemHandlerHelper.insertItem(currentEntry.getKey(), cobble, false);
                        return 1;
                    }
                }
            }
            nextEntry = currentEntry;
        }
        return i;
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }

    public void nextMode(int id) {
        Map.Entry<ItemStackHandler, Mode> it = getEntry(id);
        modeList.replace(it.getKey(), Mode.values()[(Arrays.asList(Mode.values()).indexOf(it.getValue()) + 1) % Mode.values().length]);
        if (TeslaCoreLib.INSTANCE.isClientSide()) {
            NBTTagCompound compound = this.setupSpecialNBTMessage("CHANGE_MODE");
            Map.Entry<ItemStackHandler, Mode> mode = getEntry(id);
            compound.setInteger("id", id);
            compound.setString("value", mode.getValue().toString());
            this.sendToServer(compound);
        }
    }

    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        super.processClientMessage(messageType, compound);
        if (messageType.equals("CHANGE_MODE")) {
            Map.Entry<ItemStackHandler, Mode> entry = getEntry(compound.getInteger("id"));
            modeList.replace(entry.getKey(), Mode.valueOf(compound.getString("value")));
        }
        return null;
    }

    public Map.Entry<ItemStackHandler, Mode> getEntry(int id) {
        return Iterators.get(ImmutableList.copyOf(modeList.entrySet()).iterator(), id);
    }

    public enum Mode {
        NONE(new ItemStack(Blocks.BARRIER), "text.industrialforegoing.button.stone.stopped"),
        FURNACE(new ItemStack(Blocks.FURNACE), "text.industrialforegoing.button.stone.furnace"),
        CRAFT_SMALL(new ItemStack(Blocks.PLANKS), "text.industrialforegoing.button.stone.two_craft"),
        GRIND(new ItemStack(Items.DIAMOND_PICKAXE), "text.industrialforegoing.button.stone.grind"),
        CRAFT_BIG(new ItemStack(Blocks.CRAFTING_TABLE), "text.industrialforegoing.button.stone.three_craft");

        @Getter
        private ItemStack itemStack;
        @Getter
        private String name;

        Mode(ItemStack itemStack, String name) {
            this.itemStack = itemStack;
            this.name = name;
        }
    }
}
