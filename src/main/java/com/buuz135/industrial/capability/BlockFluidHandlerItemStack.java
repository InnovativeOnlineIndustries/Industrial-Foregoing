package com.buuz135.industrial.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;

public class BlockFluidHandlerItemStack extends FluidHandlerItemStack.SwapEmpty {

    private String tagName;

    public BlockFluidHandlerItemStack(ItemStack container, ItemStack emptyContainer, int capacity, String tagName) {
        super(container, emptyContainer, capacity);
        this.tagName = tagName;
    }
    @Override
    @Nonnull
    public FluidStack getFluid()
    {
        CompoundNBT tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains("BlockEntityTag") || !tagCompound.getCompound("BlockEntityTag").contains(tagName))
        {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("BlockEntityTag").getCompound(tagName));
    }

    @Override
    protected void setFluid(FluidStack fluid)
    {
        if (!container.hasTag())
        {
            CompoundNBT compoundNBT = new CompoundNBT();
            CompoundNBT blockEntityTag = new CompoundNBT();
            compoundNBT.put("BlockEntityTag", blockEntityTag);
            container.setTag(compoundNBT);
        }

        CompoundNBT fluidTag = new CompoundNBT();
        fluid.writeToNBT(fluidTag);
        container.getTag().getCompound("BlockEntityTag").put(tagName, fluidTag);
    }
}
