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
