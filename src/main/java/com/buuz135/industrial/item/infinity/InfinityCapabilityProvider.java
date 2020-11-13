package com.buuz135.industrial.item.infinity;

import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.capability.IStackHolder;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.capability.CapabilityItemStackHolder;
import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import com.hrznstudio.titanium.capability.ItemStackHolderCapability;
import com.hrznstudio.titanium.client.screen.addon.TankScreenAddon;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


public class InfinityCapabilityProvider implements ICapabilityProvider {

    private final FluidHandlerScreenProviderItemStack tank;
    private final InfinityEnergyStorage energyStorage;
    private final ItemStackHolderCapability itemStackHolder;
    private final LazyOptional<IEnergyStorage> energyStorageCap;
    private final LazyOptional<IFluidHandlerItem> tankCap;
    private final LazyOptional<IStackHolder> stackCap;


    public InfinityCapabilityProvider(ItemStack stack) {
        tank = new FluidHandlerScreenProviderItemStack(stack, 1_000_000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid != null && fluid.getFluid() != null && fluid.getFluid().equals(ModuleCore.BIOFUEL.getSourceFluid());
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluid) {
                return false;
            }

            @Nonnull
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new TankScreenAddon(30, 20, tank, FluidTankComponent.Type.NORMAL));
            }
        };
        energyStorage = new InfinityEnergyStorage(InfinityTier.ARTIFACT.getPowerNeeded(), 10, 20) {
            @Override
            public long getLongEnergyStored() {
                if (stack.hasTag()) {
                    return Math.min(stack.getTag().getLong("Energy"), InfinityTier.ARTIFACT.getPowerNeeded());
                } else {
                    return 0;
                }
            }

            @Override
            public void setEnergyStored(long energy) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundNBT());
                }
                stack.getTag().putLong("Energy", Math.min(energy, InfinityTier.ARTIFACT.getPowerNeeded()));
            }

            @Override
            public boolean canReceive() {
                return ItemInfinity.canCharge(stack);
            }
        };
        this.itemStackHolder = new InfinityStackHolder();
        this.tankCap = LazyOptional.of(() -> tank);
        this.energyStorageCap = LazyOptional.of(() -> energyStorage);
        this.stackCap = LazyOptional.of(() -> itemStackHolder);
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return tankCap.cast();
        if (capability == CapabilityEnergy.ENERGY) return energyStorageCap.cast();
        if (capability == CapabilityItemStackHolder.ITEMSTACK_HOLDER_CAPABILITY) return stackCap.cast();
        return LazyOptional.empty();
    }

}
