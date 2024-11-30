package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.gui.component.ItemStackTankScreenAddon;
import com.buuz135.industrial.utils.IFAttachments;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class InfinityTankStorage implements IFluidHandlerItem, IScreenAddonProvider {


    private final ItemStack stack;
    private List<TankDefinition> tanks;
    private HashMap<String, FluidTank> tankMap;

    public InfinityTankStorage(ItemStack stack, TankDefinition... tanks) {
        this.stack = stack;
        this.tanks = List.of(tanks);
        this.tankMap = new HashMap<>();
        var component = stack.getOrDefault(IFAttachments.INFINITY_TANKS, new CompoundTag());
        for (TankDefinition tankDefinition : this.tanks) {
            var tank = new FluidTank(tankDefinition.capacity(), tankDefinition.filter());
            if (component.contains(tankDefinition.name())) {
                tank = tank.readFromNBT(IFAttachments.registryAccess(), component.getCompound(tankDefinition.name()));
            }
            tankMap.put(tankDefinition.name(), tank);
        }
    }

    @Override
    public ItemStack getContainer() {
        return stack;
    }

    @Override
    public int getTanks() {
        return this.tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int i) {
        return this.tankMap.get(this.tanks.get(i).name()).getFluid();
    }

    @Override
    public int getTankCapacity(int i) {
        return tanks.get(i).capacity();
    }

    @Override
    public boolean isFluidValid(int i, FluidStack fluidStack) {
        return this.tankMap.get(this.tanks.get(i).name()).isFluidValid(fluidStack);
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        for (TankDefinition tankDefinition : this.tanks) {
            if (!tankDefinition.insert()) continue;
            var tank = tankMap.get(tankDefinition.name());
            var filled = tank.fill(fluidStack, fluidAction);
            if (filled > 0) {
                if (fluidAction.execute()) {
                    save();
                }
                return filled;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        for (TankDefinition tankDefinition : this.tanks) {
            if (!tankDefinition.extract()) continue;
            var tank = tankMap.get(tankDefinition.name());
            var drained = tank.drain(fluidStack, fluidAction);
            if (!drained.isEmpty()) {
                if (fluidAction.execute()) {
                    save();
                }
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int i, FluidAction fluidAction) {
        for (TankDefinition tankDefinition : this.tanks) {
            if (!tankDefinition.extract()) continue;
            var tank = tankMap.get(tankDefinition.name());
            var drained = tank.drain(i, fluidAction);
            if (!drained.isEmpty()) {
                if (fluidAction.execute()) {
                    save();
                }
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    public void save() {
        var component = new CompoundTag();
        for (TankDefinition tankDefinition : this.tanks) {
            var tank = tankMap.get(tankDefinition.name());
            component.put(tankDefinition.name(), tank.writeToNBT(IFAttachments.registryAccess(), component.getCompound(tankDefinition.name())));
        }
        this.stack.set(IFAttachments.INFINITY_TANKS, component);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> list = new ArrayList<>();
        for (int i = 0; i < this.tanks.size(); i++) {
            var tankDefinition = this.tanks.get(i);
            int finalI = i;
            list.add(() -> new ItemStackTankScreenAddon(tankDefinition.x, tankDefinition.y, this, finalI, tankDefinition.type, tankDefinition.definedFluidStack));
        }
        return list;
    }

    public record TankDefinition(String name, int capacity, int x, int y, Predicate<FluidStack> filter, boolean extract,
                                 boolean insert, FluidTankComponent.Type type, FluidStack definedFluidStack) {
    }
}
