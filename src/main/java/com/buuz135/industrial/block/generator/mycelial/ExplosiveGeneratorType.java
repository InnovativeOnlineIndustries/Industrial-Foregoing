package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.jei.generator.MycelialGeneratorRecipe;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ExplosiveGeneratorType implements IMycelialGeneratorType{
    @Override
    public String getName() {
        return "explosive";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList((stack, integer) -> stack.getItem() == Items.GUNPOWDER || stack.getItem() == Items.TNT);
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
        return new DyeColor[]{DyeColor.RED};
    }

    @Override
    public Item getDisplay() {
        return Items.TNT;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }

    @Override
    public List<MycelialGeneratorRecipe> getRecipes() {
        List<MycelialGeneratorRecipe> recipes = new ArrayList<>();
        for (Item item : new Item[]{Items.TNT, Items.GUNPOWDER}) {
            ItemStack stack = new ItemStack(item);
            Pair<Integer, Integer> power = calculate(stack);
            recipes.add(new MycelialGeneratorRecipe(Arrays.asList(Arrays.asList(stack)), new ArrayList<>(), power.getLeft(), power.getRight()));
        }
        return recipes;
    }

    private Pair<Integer, Integer> calculate(ItemStack stack){
        if (stack.getItem() == Items.TNT) return Pair.of(160*20, 160);
        return Pair.of(20*20, 160);
    }

    @Override
    public ShapedRecipeBuilder addIngredients(ShapedRecipeBuilder recipeBuilder) {
        recipeBuilder = recipeBuilder.key('B', Blocks.TNT)
                .key('C', Items.REDSTONE_TORCH)
                .key('M', IndustrialTags.Items.MACHINE_FRAME_ADVANCED);
        return recipeBuilder;
    }

    @Override
    public void onTick(World world, BlockPos pos) {
        if (world.rand.nextBoolean()){
            world.createExplosion(null, DamageSource.GENERIC , new ExplosionContext(){
                @Override
                public boolean canExplosionDestroyBlock(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, float power) {
                    return false;
                }
            }, pos.getX(), pos.getY(), pos.getZ(), 3, false, Explosion.Mode.NONE);
        }
    }

}
