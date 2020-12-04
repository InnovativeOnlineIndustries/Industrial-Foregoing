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
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PinkGeneratorType implements IMycelialGeneratorType{
    @Override
    public String getName() {
        return "pink";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> stack.getItem().getRegistryName().getPath().contains("pink"));
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
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).shrink(1);
            return calculate(stack);
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.PINK};
    }

    @Override
    public Item getDisplay() {
        return Items.PINK_DYE;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        return ForgeRegistries.ITEMS.getValues().stream().map(ItemStack::new).filter(stack -> stack.getItem().getRegistryName().getPath().contains("pink")).map(item -> new MycelialGeneratorRecipe(Arrays.asList(Arrays.asList(item)), new ArrayList<>(), 10, 40)).collect(Collectors.toList());
    }

    private Pair<Integer, Integer> calculate(ItemStack stack){
        return Pair.of(10, 40);
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Tags.Items.DYES_PINK)
                .key('C', Blocks.PINK_WOOL)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
        return recipeBuilder;
    }

}
