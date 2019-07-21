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

import com.buuz135.industrial.proxy.client.infopiece.OreDictionaryInfoPiece;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.ItemStackUtils;
import lombok.Getter;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.SyncProviderLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class OreDictionaryConverterTile extends CustomSidedTileEntity {

    public static final String[] ACCEPTED_ENTRIES = {"ore", "ingot", "nugget", "gem", "dust", "block", "gear", "plate"};
    private static final String MOD_ID = "ModId";
    private static final String ORE_DICT = "OreDict";

    @Getter
    private ItemStackHandler filter;
    private ItemStackHandler change;
    @Getter
    private String modid;
    @Getter
    private String oreDict;

    public OreDictionaryConverterTile() {
        super(OreDictionaryConverterTile.class.getName().hashCode());
        modid = "";
        oreDict = "";
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        filter = new ItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (!world.isRemote) {
                    for (int i = 0; i < change.getSlots(); i++) {
                        ItemStack stack = change.getStackInSlot(i).copy();
                        if (!stack.isEmpty()) {
                            float f = 0.7F;
                            float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
                            float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
                            float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
                            EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1 + 1, pos.getZ() + d2, stack);
                            entityitem.setDefaultPickupDelay();
                            if (stack.hasTagCompound()) {
                                entityitem.getItem().setTagCompound(stack.getTagCompound().copy());
                            }
                            world.spawnEntity(entityitem);
                        }
                        change.getStackInSlot(i).setCount(0);
                    }
                    if (!this.getStackInSlot(0).isEmpty()) {
                        List<String> dicts = ItemStackUtils.getOreDictionaryEntries(this.getStackInSlot(0));
                        if (dicts.size() > 0) {
                            modid = this.getStackInSlot(0).getItem().getRegistryName().getNamespace();
                            oreDict = dicts.get(0);
                        }
                    }
                    OreDictionaryConverterTile.this.forceSync();
                }
            }
        };
        this.addInventory(new CustomColoredItemHandler(filter, EnumDyeColor.BLUE, "filter", 18, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ItemStackUtils.getOreDictionaryEntries(stack).stream().anyMatch(s -> Arrays.stream(ACCEPTED_ENTRIES).anyMatch(s::startsWith));
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(filter, "filter");
        change = new ItemStackHandler(9) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (!this.getStackInSlot(slot).isEmpty() && !filter.getStackInSlot(0).isItemEqual(this.getStackInSlot(slot))) {
                    int size = this.getStackInSlot(slot).getCount();
                    this.setStackInSlot(slot, ItemStack.EMPTY);
                    ItemStack stack = filter.getStackInSlot(0).copy();
                    stack.setCount(size);
                    ItemHandlerHelper.insertItem(change, stack, false);
                    forceSync();
                }
            }
        };
        this.addInventory(new CustomColoredItemHandler(change, EnumDyeColor.ORANGE, "change", 18, 25 + 18 * 2, 9, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return !filter.getStackInSlot(0).isEmpty() && !filter.getStackInSlot(0).isItemEqual(stack) && ItemStackUtils.getOreDictionaryEntries(stack).contains(oreDict);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        registerSyncStringPart(MOD_ID, nbtTagString -> modid = nbtTagString.getString(), () -> new NBTTagString(modid), SyncProviderLevel.GUI);
        registerSyncStringPart(ORE_DICT, nbtTagString -> oreDict = nbtTagString.getString(), () -> new NBTTagString(oreDict), SyncProviderLevel.GUI);
    }

    @Override
    protected void innerUpdate() {

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        modid = compound.getString(MOD_ID);
        oreDict = compound.getString(ORE_DICT);
        super.readFromNBT(compound);
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound compound1 = super.writeToNBT(compound);
        compound1.setString(MOD_ID, modid);
        compound1.setString(ORE_DICT, oreDict);
        return compound1;
    }

    @NotNull
    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer<?> container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new OreDictionaryInfoPiece(this, 42, 21));
        return pieces;
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    @Override
    protected boolean getShowRedstoneControlPiece() {
        return false;
    }
}
