package com.buuz135.industrial.item;

import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class MeatFeederItem extends IFCustomItem {

    public MeatFeederItem() {
        super("meat_feeder");
    }


    @Override
    public void register() {
        super.register();
    }

    //private FluidHandlerItemStack handlerItemStack = new FluidHandlerItemStack(new ItemStack(this),8000);

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        FluidHandlerItemStack handlerItemStack = new FluidHandlerItemStack(stack, 8000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid.getFluid().equals(FluidsRegistry.MEAT);
            }

            @Override
            protected void setContainerToEmpty() {

            }
        };
        handlerItemStack.fill(new FluidStack(FluidsRegistry.MEAT, 0), true);
        return handlerItemStack;
    }
//
//    @Override
//    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
//        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
//    }
//
//    @Nullable
//    @Override
//    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
//        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return (T) handlerItemStack;
//        return null;
//    }


    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.DOWN);
        tooltip.add((handlerItemStack.getFluid() == null ? 0 : handlerItemStack.getFluid().amount) + "/" + handlerItemStack.getTankProperties()[0].getCapacity() + "mb of Meat");

    }
}
