package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FrostyGeneratorType implements IMycelialGeneratorType{
    @Override
    public String getName() {
        return "frosty";
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
        if (inputs.length > 0 && inputs[0] instanceof SidedInventoryComponent && ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).getCount() > 0){
            ItemStack stack = ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).copy();
            ((SidedInventoryComponent<?>) inputs[0]).getStackInSlot(0).shrink(1);
            return calculate(stack);
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.LIGHT_BLUE};
    }

    @Override
    public Item getDisplay() {
        return Items.SNOWBALL;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        for (Item item : new Item[]{Items.ICE, Items.PACKED_ICE, Items.SNOWBALL, Items.SNOW_BLOCK, Items.SNOW, Items.BLUE_ICE}) {
            ItemStack stack = new ItemStack(item);
            Pair<Integer, Integer> power = calculate(stack);
            recipes.add(new MycelialGeneratorRecipe(Collections.singletonList(Collections.singletonList(Ingredient.fromStacks(stack))), new ArrayList<>(), power.getLeft(), power.getRight()));
        }
        return recipes;
    }

    private Pair<Integer, Integer> calculate(ItemStack stack){
        if (stack.getItem() == Items.ICE) return Pair.of(20*20, 40);
        if (stack.getItem() == Items.PACKED_ICE) return Pair.of(20*20, 40);
        if (stack.getItem() == Items.BLUE_ICE) return Pair.of(20*20*9, 40);
        if (stack.getItem() == Items.SNOWBALL) return Pair.of(20*20, 5);
        if (stack.getItem() == Items.SNOW_BLOCK) return Pair.of(20*20, 20);
        if (stack.getItem() == Items.SNOW) return Pair.of(25*20, 2);
        return Pair.of(0, 160);
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Items.SNOWBALL)
                .key('C', Blocks.ICE)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_SIMPLE);
        return recipeBuilder;
    }

    @Override
    public void onTick(World world, BlockPos pos) {
        AxisAlignedBB area = new AxisAlignedBB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
        for (LivingEntity livingEntity : world.getEntitiesWithinAABB(LivingEntity.class, area)) {
            livingEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 10, 4));
        }
    }

}
