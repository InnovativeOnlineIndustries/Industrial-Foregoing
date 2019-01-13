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
package com.buuz135.industrial.tile.magic;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;


public class EnchantmentExtractorTile extends CustomElectricMachine {

    private ItemStackHandler inBook;
    private ItemStackHandler inEnchanted;
    private ItemStackHandler outItem;
    private ItemStackHandler outEnchanted;

    public EnchantmentExtractorTile() {
        super(EnchantmentExtractorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        initInputInv();
        initOutputInv();
    }

    private void initInputInv() {
        inBook = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }
        };

        super.addInventory(new CustomColoredItemHandler(this.inBook, EnumDyeColor.BROWN, "Input books", 18 * 3, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.BOOK);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        super.addInventoryToStorage(this.inBook, "ench_books");

        inEnchanted = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(this.inEnchanted, EnumDyeColor.PURPLE, "Input enchanted items", 18 * 3, 25 + 18 * 2, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.isItemEnchanted() && stack.getItem().isEnchantable(stack) || stack.getItem().equals(Items.ENCHANTED_BOOK);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(this.inEnchanted, "ench_ext_in_items");
    }


    private void initOutputInv() {
        outEnchanted = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outEnchanted, EnumDyeColor.MAGENTA, "Enchanted Books", 18 * 4 + 14, 25, 4, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outEnchanted, "ench_ext_out_book");

        outItem = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentExtractorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItem, EnumDyeColor.YELLOW, "Enchantless Items", 18 * 4 + 14, 25 + 18 * 2, 4, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outItem, "ench_ext_out_items");
    }

    private boolean hasBooks() {
        return !inBook.getStackInSlot(0).isEmpty();
    }

    private ItemStack getItem() {
        return inEnchanted.getStackInSlot(0);
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (getItem().getItem().equals(Items.ENCHANTED_BOOK) && ItemEnchantedBook.getEnchantments(getItem()).tagCount() == 1 && ItemHandlerHelper.insertItem(outEnchanted, getItem().copy(), true).isEmpty()) {
            ItemHandlerHelper.insertItem(outEnchanted, getItem().copy(), false);
            getItem().setCount(0);
            return 1;
        }
        if (!hasBooks() || getItem().isEmpty()) return 0;
        ItemStack enchantedItem = getItem();
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        if (ItemHandlerHelper.insertItem(outEnchanted, enchantedBook, true).isEmpty() && ItemHandlerHelper.insertItem(outItem, enchantedItem, true).isEmpty()) {
            NBTTagCompound base = null;
            if (enchantedItem.getItem().equals(Items.ENCHANTED_BOOK) && ItemEnchantedBook.getEnchantments(enchantedItem).tagCount() > 0) {
                for (int i = 0; i < ItemEnchantedBook.getEnchantments(enchantedItem).tagCount(); i++) {
                    if (ItemEnchantedBook.getEnchantments(enchantedItem).get(i) instanceof NBTTagCompound) {
                        base = (NBTTagCompound) ItemEnchantedBook.getEnchantments(enchantedItem).get(i);
                        NBTTagCompound tagCompound = enchantedItem.getTagCompound();
                        NBTTagList list = ItemEnchantedBook.getEnchantments(enchantedItem);
                        list.removeTag(i);
                        tagCompound.setTag("StoredEnchantments", list);
                        enchantedItem.setTagCompound(tagCompound);
                        break;
                    }
                }
            } else if (enchantedItem.getEnchantmentTagList().tagCount() > 0 && enchantedItem.getEnchantmentTagList().get(0) instanceof NBTTagCompound) {
                base = (NBTTagCompound) enchantedItem.getEnchantmentTagList().get(0);
                enchantedItem.getEnchantmentTagList().removeTag(0);
            }
            if (base != null && Enchantment.getEnchantmentByID(base.getShort("id")) != null) {
                ItemEnchantedBook.addEnchantment(enchantedBook, new EnchantmentData(Enchantment.getEnchantmentByID(base.getShort("id")), base.getShort("lvl")));
                if (enchantedItem.getEnchantmentTagList().isEmpty() && enchantedItem.getTagCompound() != null && enchantedItem.getTagCompound().hasKey("ench")) {
                    enchantedItem.getTagCompound().removeTag("ench");
                    if (enchantedItem.getTagCompound().isEmpty()) {
                        enchantedItem.setTagCompound(null);
                    }
                }
                ItemHandlerHelper.insertItem(outEnchanted, enchantedBook, false);
                inBook.getStackInSlot(0).setCount(inBook.getStackInSlot(0).getCount() - 1);
                if ((enchantedItem.getItem().equals(Items.ENCHANTED_BOOK) && ItemEnchantedBook.getEnchantments(enchantedItem).tagCount() == 1) || (!enchantedItem.getItem().equals(Items.ENCHANTED_BOOK) && !enchantedItem.isItemEnchanted())) {
                    ItemStack stack = enchantedItem.copy();
                    stack.setRepairCost((stack.getRepairCost() - 1) / 2);
                    if (stack.getTagCompound() != null && stack.getRepairCost() <= 0) {
                        stack.getTagCompound().removeTag("RepairCost");
                        if (stack.getTagCompound().isEmpty()) stack.setTagCompound(null);
                    }
                    ItemHandlerHelper.insertItem(outItem, stack, false);
                    enchantedItem.setCount(enchantedItem.getCount() - 1);
                }
            }
            return 1;
        }
        return 0;
    }

}
