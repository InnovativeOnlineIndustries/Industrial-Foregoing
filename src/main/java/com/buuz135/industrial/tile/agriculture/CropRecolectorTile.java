package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CropRecolectorBlock;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
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

import java.util.ArrayList;
import java.util.List;

public class CropRecolectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";

    private IFluidTank sludge;
    private ItemStackHandler outItems;
    private int pointer;

    public CropRecolectorTile() {
        super(CropRecolectorTile.class.getName().hashCode(), 1, 0);
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
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(getRadius(), 0, getRadius());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return 0;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        boolean needPointerIncrease = true;
        if (pointer < blockPos.size()) {
            BlockPos pos = blockPos.get(pointer);
            IBlockState state = this.world.getBlockState(blockPos.get(pointer));
            if ((state.getBlock() instanceof BlockCrops && ((BlockCrops) state.getBlock()).isMaxAge(state)) || (state.getBlock() instanceof BlockNetherWart && state.getValue(BlockNetherWart.AGE) >= 3)) {
                List<ItemStack> drops = state.getBlock().getDrops(this.world, blockPos.get(pointer), state, 0);
                boolean canInsert = true;
                for (ItemStack stack : drops) {
                    if (!ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                        canInsert = false;
                        break;
                    }
                }
                if (canInsert) {
                    for (ItemStack stack : drops) {
                        ItemHandlerHelper.insertItem(outItems, stack, false);
                    }
                    sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation()), true);
                    this.world.setBlockToAir(blockPos.get(pointer));
                }
            } else if (BlockUtils.isLog(this.world, pos)) {
                List<BlockPos> chopped = doRecursiveChopping(world, pos, new ArrayList<>(), new ArrayList<>());
                needPointerIncrease = false;
                sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, ((CropRecolectorBlock) this.getBlockType()).getSludgeOperation() * chopped.size()), true);
            }
        } else {
            pointer = 0;
        }
        if (needPointerIncrease) ++pointer;
        if (pointer >= blockPos.size()) pointer = 0;
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

    public List<BlockPos> doRecursiveChopping(World world, BlockPos current, List<BlockPos> blocksChecked, List<BlockPos> blocksChopped) {
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP}) {
            if (BlockUtils.isLeaves(world, current.offset(facing)) && !blocksChecked.contains(current.offset(facing))) {
                blocksChecked.add(current.offset(facing));
                doRecursiveChopping(world, current.offset(facing), blocksChecked, blocksChopped);
            }
        }
        for (EnumFacing facing : new EnumFacing[]{EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP}) {
            if (BlockUtils.isLog(world, current.offset(facing)) && !blocksChecked.contains(current.offset(facing))) {
                blocksChecked.add(current.offset(facing));
                doRecursiveChopping(world, current.offset(facing), blocksChecked, blocksChopped);
            }
        }
        if (blocksChopped.size() > ((CropRecolectorBlock) this.getBlockType()).getTreeOperations())
            return blocksChopped;
        blocksChopped.add(current);
        List<ItemStack> drops = world.getBlockState(current).getBlock().getDrops(world, current, world.getBlockState(current), 0);
        boolean canInsert = true;
        for (ItemStack stack : drops) {
            if (!ItemHandlerHelper.insertItem(outItems, stack, true).isEmpty()) {
                canInsert = false;
                break;
            }
        }
        if (canInsert) {
            for (ItemStack stack : drops) {
                ItemHandlerHelper.insertItem(outItems, stack, false);
            }
            this.world.setBlockToAir(current);
        }
        return blocksChopped;
    }


}
