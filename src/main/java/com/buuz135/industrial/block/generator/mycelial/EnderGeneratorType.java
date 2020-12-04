package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class EnderGeneratorType implements IMycelialGeneratorType{
    @Override
    public String getName() {
        return "ender";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> stack.getItem() == Items.ENDER_PEARL || stack.getItem() == Items.ENDER_EYE);
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
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0){
            ItemStack ender = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).shrink(1);
            return calculate(ender);
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.GREEN};
    }

    @Override
    public Item getDisplay() {
        return Items.ENDER_EYE;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        for (Item item : new Item[]{Items.ENDER_EYE, Items.ENDER_PEARL}) {
            ItemStack stack = new ItemStack(item);
            Pair<Integer, Integer> power = calculate(stack);
            recipes.add(new MycelialGeneratorRecipe(Arrays.asList(Arrays.asList(stack)), new ArrayList<>(), power.getLeft(), power.getRight()));
        }
        return recipes;
    }

    private Pair<Integer, Integer> calculate(ItemStack stack){
        if (stack.getItem() == Items.ENDER_EYE) return Pair.of(160*20, 80);
        return Pair.of(80*20, 40);
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Items.ENDER_PEARL)
                .key('C', Items.ENDER_EYE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }

}
