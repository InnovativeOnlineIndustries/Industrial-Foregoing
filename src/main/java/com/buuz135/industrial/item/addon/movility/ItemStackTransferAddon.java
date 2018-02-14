package com.buuz135.industrial.item.addon.movility;

import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackTransferAddon extends TransferAddon {

    public ItemStackTransferAddon(ActionMode mode) {
        super("itemstack_transfer_addon", mode);
    }

    @Override
    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "pcp", "dtd", "pcp",
                'p', ItemRegistry.plastic,
                'c', "chestWood",
                'd', "dyeMagenta",
                't', this.getMode() == ActionMode.PUSH ? Blocks.PISTON : Blocks.STICKY_PISTON);
    }

    @Override
    public boolean actionTransfer(World world, BlockPos pos, EnumFacing facing, int transferAmount) {
        TileEntity tileEntityOrigin = world.getTileEntity(pos);
        TileEntity tileEntityDestination = world.getTileEntity(pos.offset(facing));
        if (tileEntityOrigin != null && tileEntityDestination != null && tileEntityOrigin.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) && tileEntityDestination.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
            IItemHandler itemHandlerOrigin = getCapabilityOriginByMode(tileEntityOrigin, tileEntityDestination, facing);
            IItemHandler itemHandlerDestination = getCapabilityDestinationByMode(tileEntityOrigin, tileEntityDestination, facing);
            for (int i = 0; i < itemHandlerOrigin.getSlots(); ++i) {
                ItemStack input = itemHandlerOrigin.extractItem(i, transferAmount, true);
                if (!input.isEmpty()) {
                    ItemStack result = ItemHandlerHelper.insertItem(itemHandlerDestination, input, true);
                    int amountInserted = input.getCount() - result.getCount();
                    if (amountInserted > 0) {
                        result = ItemHandlerHelper.insertItem(itemHandlerDestination, itemHandlerOrigin.extractItem(i, amountInserted, false), false);
                        if (!result.isEmpty()) ItemHandlerHelper.insertItem(itemHandlerOrigin, result, false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public IItemHandler getCapabilityOriginByMode(TileEntity origin, TileEntity destination, EnumFacing facing) {
        return this.getMode() == ActionMode.PUSH ? origin.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) : destination.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
    }

    public IItemHandler getCapabilityDestinationByMode(TileEntity origin, TileEntity destination, EnumFacing facing) {
        return this.getMode() == ActionMode.PUSH ? destination.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()) : origin.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
    }
}
