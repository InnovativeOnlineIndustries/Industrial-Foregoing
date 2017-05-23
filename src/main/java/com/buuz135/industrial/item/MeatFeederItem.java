package com.buuz135.industrial.item;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;
import java.util.List;

public class MeatFeederItem extends IFCustomItem {

    public MeatFeederItem() {
        super("meat_feeder");
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        FluidHandlerItemStack handlerItemStack = new FluidHandlerItemStack(stack, 64000) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid.getFluid().equals(FluidsRegistry.MEAT);
            }

        };
        handlerItemStack.fill(new FluidStack(FluidsRegistry.MEAT, 0), true);
        return handlerItemStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.DOWN);
        tooltip.add(getFilledAmount(stack) + "/" + handlerItemStack.getTankProperties()[0].getCapacity() + "mb of Meat");
    }

    public int getFilledAmount(ItemStack stack) {
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.DOWN);
        return (handlerItemStack.getFluid() == null ? 0 : handlerItemStack.getFluid().amount);
    }

    public void drain(ItemStack stack, int amount) {
        FluidHandlerItemStack handlerItemStack = (FluidHandlerItemStack) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.DOWN);
        handlerItemStack.drain(new FluidStack(FluidsRegistry.MEAT, amount), true);
    }

    @Override
    public IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this), "pip", "gig", " i ",
                'p', ItemRegistry.plastic,
                'i', "ingotIron",
                'g', Items.GLASS_BOTTLE);
    }
}
