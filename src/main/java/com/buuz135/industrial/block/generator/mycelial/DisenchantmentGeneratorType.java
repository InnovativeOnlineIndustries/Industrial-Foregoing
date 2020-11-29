package com.buuz135.industrial.block.generator.mycelial;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DisenchantmentGeneratorType implements IMycelialGeneratorType{

    @Override
    public String getName() {
        return "disenchantment";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem);
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundNBT>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0;
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0) {
            ItemStack itemstack = ((SidedInventoryComponent) inputs[0]).getStackInSlot(0);
            Map<Enchantment, Integer> ench =  EnchantmentHelper.getEnchantments(itemstack);
            int rarity = 0;
            double level = 0;
            for (Map.Entry<Enchantment, Integer> enchEntry : ench.entrySet()) {
                rarity += (14 - enchEntry.getKey().getRarity().getWeight());
                level += (enchEntry.getValue() / (double) enchEntry.getKey().getMaxLevel());
            }
            if (itemstack.getItem().equals(Items.ENCHANTED_BOOK)){
                ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, new ItemStack(Items.BOOK));
            } else {
                ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, new ItemStack(itemstack.getItem()));
            }
            return Pair.of(rarity * 160, (int) (80 * level));
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.LIME};
    }

    @Override
    public Item getDisplay() {
        return Items.ENCHANTED_BOOK;
    }
}
