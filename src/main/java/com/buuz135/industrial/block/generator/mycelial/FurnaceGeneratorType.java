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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FurnaceGeneratorType implements IMycelialGeneratorType {

    @Override
    public String getName() {
        return "furnace";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Collections.singletonList((stack, slot) -> ForgeHooks.getBurnTime(stack) > 0);
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
            int burnTime = ForgeHooks.getBurnTime(itemstack);
            if (itemstack.hasContainerItem())
                ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, itemstack.getContainerItem());
            else if (!itemstack.isEmpty()) {
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    ((SidedInventoryComponent) inputs[0]).setStackInSlot(0, itemstack.getContainerItem());
                }
            }
            return Pair.of(burnTime, 80);
        }
        return Pair.of(0, 80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.BLUE};
    }

    @Override
    public Item getDisplay() {
        return Items.FURNACE;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        return ForgeRegistries.ITEMS.getValues().stream().map(ItemStack::new).filter(stack -> ForgeHooks.getBurnTime(stack) > 0).map(item -> new MycelialGeneratorRecipe(Collections.singletonList(Collections.singletonList(Ingredient.fromStacks(item))), new ArrayList<>(), ForgeHooks.getBurnTime(item), 80)).collect(Collectors.toList());
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Tags.Items.STORAGE_BLOCKS_COAL)
                .key('C', Blocks.FURNACE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
        return recipeBuilder;
    }
}
