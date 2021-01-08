package com.buuz135.industrial.item.infinity;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.capability.IStackHolder;
import com.hrznstudio.titanium.capability.CapabilityItemStackHolder;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import com.hrznstudio.titanium.capability.ItemStackHolderCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class InfinityCapabilityProvider implements ICapabilityProvider {

    private final FluidHandlerScreenProviderItemStack tank;
    private final InfinityEnergyStorage energyStorage;
    private final ItemStackHolderCapability itemStackHolder;
    private final LazyOptional<IEnergyStorage> energyStorageCap;
    private final LazyOptional<IFluidHandlerItem> tankCap;
    private final LazyOptional<IStackHolder> stackCap;
    private final ItemStack stack;

    public InfinityCapabilityProvider(ItemStack stack, IFactory<? extends FluidHandlerScreenProviderItemStack> tankFactory, IFactory<InfinityEnergyStorage> energyFactory) {
        this.tank = tankFactory.create();
        this.energyStorage = energyFactory.create();
        this.itemStackHolder = new InfinityStackHolder();
        this.tankCap = LazyOptional.of(() -> tank);
        this.energyStorageCap = LazyOptional.of(() -> energyStorage);
        this.stackCap = LazyOptional.of(() -> itemStackHolder);
        this.stack = stack;
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return tankCap.cast();
        if (capability == CapabilityEnergy.ENERGY) return energyStorageCap.cast();
        if (capability == CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY) return stackCap.cast();
        return LazyOptional.empty();
    }

    public ItemStack getStack() {
        return stack;
    }
}
