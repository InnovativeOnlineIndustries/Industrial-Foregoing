package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.jei.generator.MycelialGeneratorRecipe;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PotionGeneratorType implements IMycelialGeneratorType{

    @Override
    public String getName() {
        return "potion";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, slot) -> stack.getItem() instanceof PotionItem);
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return new ArrayList<>();
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundNBT>[] inputs) {
        return inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0 && getSlotInputPredicates().get(0).test(((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0), 0);
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs) {
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0){
            ItemStack calculate = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0);
            if (stack.getCount() == 1){
                ((SidedInventoryComponent<?>) inputs[0]).setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                stack.shrink(1);
            }
            return calculate(calculate);
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.PURPLE};
    }

    @Override
    public Item getDisplay() {
        return Items.POTION;
    }

    @Override
    public int getSlotSize() {
        return 1;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        return ForgeRegistries.POTION_TYPES.getValues().stream().filter(potion -> potion != Potions.EMPTY).map(effect -> Arrays.asList(
                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), effect),
                PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), effect),
                PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), effect)
                ))
                .flatMap(Collection::stream)
                .map(stack -> new MycelialGeneratorRecipe(Arrays.asList(Arrays.asList(stack)), new ArrayList<>(), calculate(stack).getLeft(), calculate(stack).getRight())).collect(Collectors.toList());
    }

    private Pair<Integer,Integer> calculate(ItemStack stack){
        Potion potion = PotionUtils.getPotionFromItem(stack);
        int duration = 80;
        int amplifier = 1;
        for (EffectInstance potionEffect : potion.getEffects()) {
            duration += potionEffect.getDuration();
            amplifier += potionEffect.getAmplifier();
        }
        double powValue = 2;
        if (stack.getItem() instanceof ThrowablePotionItem) powValue = 2.5;
        if (stack.getItem() instanceof LingeringPotionItem) powValue = 3;
        return Pair.of(duration,  (int) (Math.pow(amplifier, powValue) * 10));
    }
}
