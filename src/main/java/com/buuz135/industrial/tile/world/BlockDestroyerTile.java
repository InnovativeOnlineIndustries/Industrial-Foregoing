package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class BlockDestroyerTile extends WorkingAreaElectricMachine {


    private ItemStackHandler outItems;

    public BlockDestroyerTile() {
        super(BlockDestroyerTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outItems = new ItemStackHandler(3 * 6) {
            @Override
            protected void onContentsChanged(int slot) {
                BlockDestroyerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output items", 18 * 3, 25, 6, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outItems, "block_destroyer_out");
    }


    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea());
        for (BlockPos pos : blockPosList) {
            if (this.world.isAirBlock(pos)) continue;
            if (BlockUtils.canBlockBeBroken(this.world, pos)) {
                Block block = this.world.getBlockState(pos).getBlock();
                TileEntity tile = world.getTileEntity(pos);
                if (block.getBlockHardness(this.world.getBlockState(pos), this.world, pos) < 0) continue;
                List<ItemStack> drops = block.getDrops(this.world, pos, this.world.getBlockState(pos), 0);
                boolean canInsert = true;
                for (ItemStack stack : drops) {
                    if (tile instanceof IWorldNameable) {
                        if (((IWorldNameable) tile).hasCustomName()) {
                            stack.setStackDisplayName(((IWorldNameable) tile).getName());
                        }
                    }
                    if (!ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                        canInsert = false;
                        break;
                    }
                }
                if (canInsert) {
                    for (ItemStack stack : drops) {
                        ItemHandlerHelper.insertItem(outItems, stack, false);
                    }
                    if (tile instanceof TileEntityShulkerBox) {
                        InventoryHelper.dropInventoryItems(this.world, pos, (IInventory) tile);
                        ((TileEntityShulkerBox) tile).clear();
                    }
                    this.world.setBlockToAir(pos);
                    return 1;
                }
            }
        }
        return 0;
    }
}
