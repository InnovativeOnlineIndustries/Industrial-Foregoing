/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.capability;

import com.buuz135.industrial.gui.component.ItemStackTankScreenAddon;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MultipleFluidHandlerScreenProviderItemStack extends FluidHandlerScreenProviderItemStack {

    private static String NBT_TANKS = "Tanks";

    private TankDefinition[] tankDefinitions;

    public MultipleFluidHandlerScreenProviderItemStack(@Nonnull ItemStack container, int capacity, TankDefinition... tankDefinitions) {
        super(container, capacity);
        this.tankDefinitions = tankDefinitions;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return Arrays.stream(tankDefinitions).anyMatch(tankDefinition -> tankDefinition.validator.test(stack));
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        for (int i = 0; i < tankDefinitions.length; i++) {
            if (tankDefinitions[i].canDrain) return getFluidInTank(i);
        }
        return super.getFluid();
    }

    @Override
    public int getFluidAmount() {
        for (int i = 0; i < tankDefinitions.length; i++) {
            if (tankDefinitions[i].canDrain) return getFluidInTank(i).getAmount();
        }
        return super.getFluidAmount();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        CompoundTag compoundNBT = getContainer().getOrCreateTagElement(NBT_TANKS);
        if (!compoundNBT.contains(tankDefinitions[tank].name)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(compoundNBT.getCompound(tankDefinitions[tank].name));
    }

    public void setFluidInTank(int tank, FluidStack fluidStack) {
        CompoundTag compoundNBT = getContainer().getOrCreateTagElement(NBT_TANKS);
        compoundNBT.put(tankDefinitions[tank].name, fluidStack.writeToNBT(new CompoundTag()));
    }

    @Override
    public int getTanks() {
        return tankDefinitions.length;
    }

    @Override
    public int fill(FluidStack resource, FluidAction doFill) {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource)) {
            return 0;
        }
        for (int i = 0; i < tankDefinitions.length; i++) {
            if (tankDefinitions[i].canFill && tankDefinitions[i].validator.test(resource)) {
                FluidStack contained = getFluidInTank(i);
                if (contained.isEmpty()) {
                    int fillAmount = Math.min(capacity, resource.getAmount());
                    if (doFill.execute()) {
                        FluidStack filled = resource.copy();
                        filled.setAmount(fillAmount);
                        setFluidInTank(i, filled);
                    }
                    return fillAmount;
                } else {
                    if (contained.isFluidEqual(resource)) {
                        int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());
                        if (doFill.execute() && fillAmount > 0) {
                            contained.grow(fillAmount);
                            setFluidInTank(i, contained);
                        }
                        return fillAmount;
                    }
                    return 0;
                }
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluid())) {
            return FluidStack.EMPTY;
        }
        return this.drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        for (int i = 0; i < tankDefinitions.length; i++) {
            if (tankDefinitions[i].canDrain) {
                FluidStack contained = getFluidInTank(i);
                if (contained.isEmpty()) {
                    return FluidStack.EMPTY;
                }
                final int drainAmount = Math.min(contained.getAmount(), maxDrain);
                FluidStack drained = contained.copy();
                drained.setAmount(drainAmount);
                if (action.execute()) {
                    contained.shrink(drainAmount);
                    if (contained.isEmpty()) {
                        setFluidInTank(i, FluidStack.EMPTY);
                    } else {
                        setFluidInTank(i, contained);
                    }
                }
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> addons = new ArrayList<>();
        for (int i = 0; i < tankDefinitions.length; i++) {
            int finalI = i;
            addons.add(() -> new ItemStackTankScreenAddon(tankDefinitions[finalI].x, tankDefinitions[finalI].y, this, finalI, tankDefinitions[finalI].type));
        }
        return addons;
    }

    public static class TankDefinition {

        private final String name;
        private final int x;
        private final int y;
        private final Predicate<FluidStack> validator;
        private final boolean canDrain;
        private final boolean canFill;
        private final FluidTankComponent.Type type;

        public TankDefinition(String name, int x, int y, Predicate<FluidStack> validator, boolean canDrain, boolean canFill, FluidTankComponent.Type type) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.validator = validator;
            this.canDrain = canDrain;
            this.canFill = canFill;
            this.type = type;
        }
    }
}
