/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.item.addon.movility;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;


public class FluidTransferAddon extends TransferAddon {

    public FluidTransferAddon(ActionMode mode) {
        super("fluid_transfer_addon", mode);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pcp", "dtd", "pcp",
                'p', ItemRegistry.plastic,
                'c', Items.BUCKET,
                'd', "dyeLightBlue",
                't', this.getMode() == ActionMode.PUSH ? Blocks.PISTON : Blocks.STICKY_PISTON);
    }

    @Override
    public boolean actionTransfer(World world, BlockPos pos, EnumFacing facing, int transferAmount) {
        TileEntity tileEntityOrigin = world.getTileEntity(pos);
        TileEntity tileEntityDestination = world.getTileEntity(pos.offset(facing));
        if (tileEntityOrigin != null && tileEntityDestination != null && tileEntityOrigin.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing) && tileEntityDestination.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
            IFluidHandler fluidHandlerOrigin = getCapabilityOriginByMode(tileEntityOrigin, tileEntityDestination, facing);
            IFluidHandler fluidHandlerDestination = getCapabilityDestinationByMode(tileEntityOrigin, tileEntityDestination, facing);
            FluidStack fluidStack = fluidHandlerOrigin.drain(transferAmount * 100, false);
            if (fluidStack != null) {
                fluidHandlerOrigin.drain(fluidHandlerDestination.fill(fluidStack, true), true);
                return true;
            }
        }
        return false;
    }

    public IFluidHandler getCapabilityOriginByMode(TileEntity origin, TileEntity destination, EnumFacing facing) {
        return this.getMode() == ActionMode.PUSH ? origin.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing) : destination.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
    }

    public IFluidHandler getCapabilityDestinationByMode(TileEntity origin, TileEntity destination, EnumFacing facing) {
        return this.getMode() == ActionMode.PUSH ? destination.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()) : origin.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
    }
}
