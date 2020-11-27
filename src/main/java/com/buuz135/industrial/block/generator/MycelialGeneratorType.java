package com.buuz135.industrial.block.generator;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public enum MycelialGeneratorType {

    FURNACE("furnace", new Input[]{Input.SLOT}, new BiPredicate[]{(o, o2) -> ForgeHooks.getBurnTime((ItemStack) o) > 0}, new Predicate[0],
            inbtSerializables -> {
                if (inbtSerializables.length > 0 && inbtSerializables[0] instanceof SidedInventoryComponent) {
                    ItemStack itemstack = ((SidedInventoryComponent) inbtSerializables[0]).getStackInSlot(0);
                    int burnTime = ForgeHooks.getBurnTime(itemstack);
                    if (itemstack.hasContainerItem())
                        ((SidedInventoryComponent) inbtSerializables[0]).setStackInSlot(0, itemstack.getContainerItem());
                    else if (!itemstack.isEmpty()) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            ((SidedInventoryComponent) inbtSerializables[0]).setStackInSlot(1, itemstack.getContainerItem());
                        }
                    }
                    return Pair.of(burnTime, 80);
                }
                return Pair.of(0, 80);
            });

    private final String name;
    private final Input[] inputs;
    private final BiPredicate<ItemStack, Integer>[] slotInputPredicates;
    private final Predicate<FluidStack>[] tankInputPredicates;
    private final Function<INBTSerializable<CompoundNBT>[], Pair<Integer, Integer>> powerTimeGeneration;

    MycelialGeneratorType(String name, Input[] inputs, BiPredicate<ItemStack, Integer>[] slotInputPredicates, Predicate<FluidStack>[] tankInputPredicates, Function<INBTSerializable<CompoundNBT>[], Pair<Integer, Integer>> powerTimeGeneration) {
        this.name = name;
        this.inputs = inputs;
        this.slotInputPredicates = slotInputPredicates;
        this.tankInputPredicates = tankInputPredicates;
        this.powerTimeGeneration = powerTimeGeneration;
    }

    public String getName() {
        return name;
    }

    public Input[] getInputs() {
        return inputs;
    }

    public BiPredicate<ItemStack, Integer>[] getSlotInputPredicates() {
        return slotInputPredicates;
    }

    public Predicate<FluidStack>[] getTankInputPredicates() {
        return tankInputPredicates;
    }

    public Function<INBTSerializable<CompoundNBT>[], Pair<Integer, Integer>> getPowerTimeGeneration() {
        return powerTimeGeneration;
    }

    public enum Input {
        SLOT,
        TANK;
    }
}
