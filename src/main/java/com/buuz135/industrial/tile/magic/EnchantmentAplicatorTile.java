package com.buuz135.industrial.tile.magic;


import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.CustomElectricMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import javax.annotation.Nonnull;
import java.util.Map;

public class EnchantmentAplicatorTile extends CustomElectricMachine {

    private ItemStackHandler inEnchantedBook;
    private ItemStackHandler inItem;
    private ItemStackHandler outEnchantedItem;
    private IFluidTank experienceTank;

    public EnchantmentAplicatorTile() {
        super(EnchantmentAplicatorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        this.initInputInv();
        this.initOutputInv();
    }

    private void initInputInv() {
        this.experienceTank = this.addFluidTank(FluidsRegistry.ESSENCE, 32000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));

        inEnchantedBook = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(this.inEnchantedBook, EnumDyeColor.PURPLE, "Input enchanted books", 18 * 5 + 10, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(Items.ENCHANTED_BOOK);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

        });
        this.addInventoryToStorage(this.inEnchantedBook, "ench_aplic_in_book");

        inItem = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }

            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }
        };
        this.addInventory(new CustomColoredItemHandler(this.inItem, EnumDyeColor.GREEN, "Input items", 18 * 7 + 10, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().isEnchantable(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }
        });
        this.addInventoryToStorage(this.inItem, "ench_aplic_in_item");

    }

    private void initOutputInv() {
        outEnchantedItem = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantmentAplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outEnchantedItem, EnumDyeColor.MAGENTA, "Enchanted items", 18 * 5 + 10, 25 + 18 * 2, 3, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outEnchantedItem, "ench_aplic_out_item");

    }

    public double getLevels() {
        NBTTagList list = ((ItemEnchantedBook) inEnchantedBook.getStackInSlot(0).getItem()).getEnchantments(inEnchantedBook.getStackInSlot(0));
        double amount = 0;
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound compound = ((NBTTagCompound) list.get(i));
            amount += EnchantmentHelper.calcItemStackEnchantability(this.world.rand, compound.getInteger("id"), compound.getShort("lvl"), inItem.getStackInSlot(0));

        }
        return amount;
    }

    public boolean canWork() {
        return !inItem.getStackInSlot(0).isEmpty() && !inEnchantedBook.getStackInSlot(0).isEmpty();
    }

    @Override
    protected float performWork() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (!canWork()) return 0;
        int xp = (int) (getLevels() * 100);
        if (experienceTank.getFluidAmount() >= xp && ItemHandlerHelper.insertItem(outEnchantedItem, inItem.getStackInSlot(0), true).isEmpty()) {
            Map<Enchantment, Integer> mapFirst = EnchantmentHelper.getEnchantments(inEnchantedBook.getStackInSlot(0));
            Map<Enchantment, Integer> mapSecond = EnchantmentHelper.getEnchantments(inItem.getStackInSlot(0));
            for (Enchantment enchantmentFirst : mapFirst.keySet()) {
                if (enchantmentFirst != null) {
                    if (mapSecond.containsKey(enchantmentFirst) && mapFirst.get(enchantmentFirst) == mapSecond.get(enchantmentFirst) && mapFirst.get(enchantmentFirst) >= enchantmentFirst.getMaxLevel())
                        return 0;
                    int value = mapSecond.containsKey(enchantmentFirst) && mapFirst.get(enchantmentFirst) == mapSecond.get(enchantmentFirst) ? Math.min(mapFirst.get(enchantmentFirst) + 1, enchantmentFirst.getMaxLevel()) : mapFirst.get(enchantmentFirst);
                    if (mapSecond.replace(enchantmentFirst, value) == null) {
                        mapSecond.put(enchantmentFirst, value);
                    }
                }
            }
            ItemStack stack = inItem.getStackInSlot(0).copy();
            EnchantmentHelper.setEnchantments(mapSecond, stack);
            ItemHandlerHelper.insertItem(outEnchantedItem, stack, false);
            inItem.getStackInSlot(0).setCount(0);
            inEnchantedBook.getStackInSlot(0).setCount(0);
            experienceTank.drain(xp, true);
            return 500;
        }
        return 0;
    }
}
