package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CropRecolectorBlock;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.*;

public class CropRecolectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";

    private IFluidTank sludge;
    private ItemStackHandler outItems;
    private int pointer;
    private Queue<BlockPos> woodCache;
    private Queue<BlockPos> leavesCache;


    public CropRecolectorTile() {
        super(CropRecolectorTile.class.getName().hashCode(), 1, 0, true);
        woodCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), pos.getY(), ((BlockPos) value).getZ())).reversed());
        leavesCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSq(((BlockPos) value).getX(), pos.getY(), ((BlockPos) value).getZ())).reversed());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        sludge = this.addFluidTank(FluidsRegistry.SLUDGE, 8000, EnumDyeColor.BLACK, "Sludge tank", new BoundingRectangle(50, 25, 18, 54));
        outItems = new ItemStackHandler(3 * 4) {
            @Override
            protected void onContentsChanged(int slot) {
                CropRecolectorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Crops output", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outItems, "outItems");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(this.getFacing().getOpposite(), getRadius() + 1);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).grow(getRadius(), 0, getRadius());
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (ItemStackUtils.isInventoryFull(outItems)) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean needPointerIncrease = true;
        boolean treeOperation = false;
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            IBlockState state = this.world.getBlockState(blockPos.get(pointer));
            if ((state.getBlock() instanceof BlockCrops && ((BlockCrops) state.getBlock()).isMaxAge(state)) || (state.getBlock() instanceof BlockNetherWart && state.getValue(BlockNetherWart.AGE) >= 3)) {
                List<ItemStack> drops = state.getBlock().getDrops(this.world, blockPos.get(pointer), state, 0);
                if (canInsertAll(drops, outItems)) {
                    insertItemsAndRemove(drops, pos, outItems);
                }
            } else if (BlockUtils.isLog(this.world, pos)) {
                if (woodCache.isEmpty()) {
                    checkForTrees(this.world, pos);
                }
                needPointerIncrease = false;
                treeOperation = true;

                for (int i = 0; i < ((CropRecolectorBlock) this.getBlockType()).getTreeOperations(); ++i) {
                    if (woodCache.isEmpty() && leavesCache.isEmpty()) break;
                    if (!leavesCache.isEmpty()) chop(leavesCache);
                    else chop(woodCache);
                }
                if (woodCache.isEmpty()) needPointerIncrease = true;
            } else if ((state.getBlock() instanceof BlockCactus || state.getBlock() instanceof BlockReed)) {
                if (state.getBlock().equals(this.world.getBlockState(pos.offset(EnumFacing.UP, 2)).getBlock())) {
                    List<ItemStack> drops = state.getBlock().getDrops(this.world, blockPos.get(pointer), state, 0);
                    if (canInsertAll(drops, outItems)) {
                        insertItemsAndRemove(drops, pos.offset(EnumFacing.UP, 2), outItems);
                    }
                }
                if (state.getBlock().equals(this.world.getBlockState(pos.offset(EnumFacing.UP, 1)).getBlock())) {
                    List<ItemStack> drops = state.getBlock().getDrops(this.world, blockPos.get(pointer), state, 0);
                    if (canInsertAll(drops, outItems)) {
                        insertItemsAndRemove(drops, pos.offset(EnumFacing.UP, 1), outItems);
                    }
                }
            }
        } else {
            pointer = 0;
        }
        if (needPointerIncrease) ++pointer;
        if (pointer >= blockPos.size()) pointer = 0;
        if (treeOperation) return -1;
        return 1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagCompound = super.writeToNBT(compound);
        tagCompound.setInteger(NBT_POINTER, pointer);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (!compound.hasKey(NBT_POINTER)) pointer = 0;
        else pointer = compound.getInteger(NBT_POINTER);
    }

    public void checkForTrees(World world, BlockPos current) {
        Stack<BlockPos> tree = new Stack<>();
        tree.push(current);
        while (!tree.isEmpty()) {
            BlockPos checking = tree.pop();
            if (BlockUtils.isLog(world, checking) || BlockUtils.isLeaves(world, checking)) {
                Iterable<BlockPos> area = BlockPos.getAllInBox(checking.offset(EnumFacing.DOWN).offset(EnumFacing.SOUTH).offset(EnumFacing.WEST), checking.offset(EnumFacing.UP).offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
                for (BlockPos blockPos : area) {
                    if (BlockUtils.isLog(world, blockPos) && !woodCache.contains(blockPos) && blockPos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= 1000) {
                        tree.push(blockPos);
                        woodCache.add(blockPos);
                    } else if (BlockUtils.isLeaves(world, blockPos) && !leavesCache.contains(blockPos) && blockPos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= 1000) {
                        tree.push(blockPos);
                        leavesCache.add(blockPos);
                    }
                }
            }
        }
    }

    private boolean canInsertAll(List<ItemStack> drops, ItemStackHandler outItems) {
        boolean canInsert = true;
        for (ItemStack stack : drops) {
            if (!ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                canInsert = false;
                break;
            }
        }
        return canInsert;
    }

    private void insertItemsAndRemove(List<ItemStack> drops, BlockPos blockPos, ItemStackHandler outItems) {
        for (ItemStack stack : drops) {
            ItemHandlerHelper.insertItem(outItems, stack, false);
        }
        sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation()), true);
        this.world.setBlockToAir(blockPos);
    }

    private void chop(Queue<BlockPos> cache) {
        BlockPos p = cache.peek();
        if (BlockUtils.isLeaves(world, p) || BlockUtils.isLog(world, p)) {
            IBlockState s = world.getBlockState(p);
            List<ItemStack> drops = s.getBlock().getDrops(world, p, s, 0);
            for (ItemStack drop : drops) {
                if (!ItemHandlerHelper.insertItem(outItems, drop, true).isEmpty()) break;
                ItemHandlerHelper.insertItem(outItems, drop, false);
            }
            world.setBlockToAir(p);
            sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation()), true);

        }
        cache.poll();
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, sludge);
    }
}
