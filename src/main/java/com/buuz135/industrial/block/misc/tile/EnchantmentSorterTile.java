package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class EnchantmentSorterTile extends IndustrialProcessingTile<EnchantmentSorterTile> {

    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> input;
    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> noEnchanted;
    @Save
    private SidedInventoryComponent<EnchantmentSorterTile> enchantedItems;

    public EnchantmentSorterTile() {
        super(ModuleMisc.ENCHANTMENT_SORTER, 57, 40);
        this.addInventory(input = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("input", 35, 40, 1, 0).
                setColor(DyeColor.BLUE).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(noEnchanted = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("noEnchanted", 90, 20, 4, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(enchantedItems = (SidedInventoryComponent<EnchantmentSorterTile>) new SidedInventoryComponent<EnchantmentSorterTile>("enchanted", 90, 60, 4, 2).
                setColor(DyeColor.MAGENTA).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @Override
    public EnchantmentSorterTile getSelf() {
        return this;
    }

    @Override
    public boolean canIncrease() {
        return !input.getStackInSlot(0).isEmpty() && ((isEnchanted(input.getStackInSlot(0)) && ItemHandlerHelper.insertItem(enchantedItems, input.getStackInSlot(0).copy(), true).isEmpty()) || (!isEnchanted(input.getStackInSlot(0)) && ItemHandlerHelper.insertItem(noEnchanted, input.getStackInSlot(0).copy(), true).isEmpty()));
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack stack = input.getStackInSlot(0).copy();
            ItemHandlerHelper.insertItem(isEnchanted(stack) ? enchantedItems : noEnchanted, stack, false);
            input.setStackInSlot(0, ItemStack.EMPTY);
        };
    }

    @Override
    protected int getTickPower() {
        return 40;
    }

    @Override
    public int getMaxProgress() {
        return 50;
    }

    private boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem;
    }
}
