package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CrimedGeneratorType implements IMycelialGeneratorType {
    @Override
    public String getName() {
        return "crimed";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> calculate(stack).getLeft() > 0);
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
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).shrink(1);
            return calculate(stack);
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.CYAN};
    }

    @Override
    public Item getDisplay() {
        return Items.WARPED_FUNGUS;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        for (Item item : new Item[]{Items.CRIMSON_FUNGUS, Items.CRIMSON_ROOTS, Items.WEEPING_VINES, Items.WARPED_FUNGUS, Items.WARPED_ROOTS, Items.NETHER_SPROUTS, Items.TWISTING_VINES, Items.SHROOMLIGHT, Items.NETHER_WART_BLOCK, Items.WARPED_WART_BLOCK}) {
            ItemStack stack = new ItemStack(item);
            Pair<Integer, Integer> power = calculate(stack);
            recipes.add(new MycelialGeneratorRecipe(Collections.singletonList(Collections.singletonList(Ingredient.fromStacks(stack))), new ArrayList<>(), power.getLeft(), power.getRight()));
        }
        return recipes;
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Blocks.NETHER_WART_BLOCK)
                .key('C', Blocks.CRIMSON_FUNGUS)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }

    private Pair<Integer, Integer> calculate(ItemStack stack) {
        if (stack.getItem() == Items.CRIMSON_FUNGUS || stack.getItem() == Items.CRIMSON_ROOTS || stack.getItem() == Items.WEEPING_VINES
                || stack.getItem() == Items.WARPED_FUNGUS || stack.getItem() == Items.WARPED_ROOTS || stack.getItem() == Items.NETHER_SPROUTS || stack.getItem() == Items.TWISTING_VINES)
            return Pair.of(10 * 20, 20);
        if (stack.getItem() == Items.SHROOMLIGHT) return Pair.of(10 * 20, 40);
        if (stack.getItem() == Items.NETHER_WART_BLOCK || stack.getItem() == Items.WARPED_WART_BLOCK)
            return Pair.of(20 * 20, 40);
        return Pair.of(0, 80);
    }

}
