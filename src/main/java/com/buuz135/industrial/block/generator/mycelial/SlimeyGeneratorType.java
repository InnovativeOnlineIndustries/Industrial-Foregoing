package com.buuz135.industrial.block.generator.mycelial;

import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SlimeyGeneratorType implements IMycelialGeneratorType{

    @Override
    public String getName() {
        return "slimey";
    }

    @Override
    public Input[] getInputs() {
        return new Input[]{Input.TANK, Input.SLOT};
    }

    @Override
    public List<BiPredicate<ItemStack, Integer>> getSlotInputPredicates() {
        return Arrays.asList(null, (stack, slot) -> stack.getItem().isIn(Tags.Items.SLIMEBALLS));
    }

    @Override
    public List<Predicate<FluidStack>> getTankInputPredicates() {
        return Arrays.asList(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MILK.getSourceFluid()), null);
    }

    @Override
    public boolean canStart(INBTSerializable<CompoundNBT>[] inputs) {
        return inputs.length >= 2 && inputs[0] instanceof SidedFluidTankComponent && inputs[1] instanceof SidedInventoryComponent && ((SidedFluidTankComponent<?>) inputs[0]).getFluidAmount() >= 250 && ((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).getCount() > 0;
    }

    @Override
    public Pair<Integer, Integer> getTimeAndPowerGeneration(INBTSerializable<CompoundNBT>[] inputs) {
        if (inputs.length >= 2 && inputs[0] instanceof SidedFluidTankComponent && inputs[1] instanceof SidedInventoryComponent){
            if (((SidedFluidTankComponent<?>) inputs[0]).getFluidAmount() >= 250 && ((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).getCount() > 0){
                ((SidedFluidTankComponent<?>) inputs[0]).drainForced(250, IFluidHandler.FluidAction.EXECUTE);
                ((SidedInventoryComponent<?>) inputs[1]).getStackInSlot(0).shrink(1);
                return Pair.of(20*20, 200);
            }
        }
        return Pair.of(0,80);
    }

    @Override
    public DyeColor[] getInputColors() {
        return new DyeColor[]{DyeColor.WHITE, DyeColor.LIME};
    }

    @Override
    public Item getDisplay() {
        return Items.SLIME_BALL;
    }

    @Override
    public int getSlotSize() {
        return 64;
    }
}
