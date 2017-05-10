package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class BlockPlacerTile extends WorkingAreaElectricMachine {

    private ItemStackHandler inItems;

    public BlockPlacerTile() {
        super(BlockPlacerTile.class.getName().hashCode(), 0, 0);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        inItems = new ItemStackHandler(3 * 6){
            @Override
            protected void onContentsChanged(int slot) {
                BlockPlacerTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(inItems, EnumDyeColor.BLUE, "Input items", 18 * 3, 25, 6,  3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(inItems, "block_destroyer_out");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(new BlockPos(0, 0, 0).offset(this.getFacing().getOpposite()));
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
        List<BlockPos> blockPosList = BlockUtils.getBlockPosInAABB(getWorkingArea());
        for (BlockPos pos : blockPosList) {
            if (this.world.isAirBlock(pos)) {
                ItemStack stack = getFirstStackHasBlock();
                if (stack.isEmpty()) return 0;
                this.world.setBlockState(pos, Block.getBlockFromItem(stack.getItem()).getDefaultState());
                stack.setCount(stack.getCount() - 1);
                return 1;
            }
        }
        return 0;
    }

    private ItemStack getFirstStackHasBlock() {
        for (int i = 0; i < inItems.getSlots(); ++i) {
            if (!inItems.getStackInSlot(i).isEmpty() && !Block.getBlockFromItem(inItems.getStackInSlot(i).getItem()).equals(Blocks.AIR))
                return inItems.getStackInSlot(i);
        }
        return ItemStack.EMPTY;
    }
}
