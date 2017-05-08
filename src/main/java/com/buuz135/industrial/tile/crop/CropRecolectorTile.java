package com.buuz135.industrial.tile.crop;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CropRecolectorTile extends WorkingAreaElectricMachine {

    private static String NBT_POINTER = "pointer";

    private ItemStackHandler outItems;
    private int pointer;

    public CropRecolectorTile() {
        super(CropRecolectorTile.class.getName().hashCode(), 1, 0);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outItems = new ItemStackHandler(3 * 6);
        this.addInventory(new ColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Crops output", new BoundingRectangle(18 * 3, 25, 18 * 6, 18 * 3)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);
                BoundingRectangle box = this.getBoundingBox();
                int i = 0;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 6; x++) {
                        slots.add(new FilteredSlot(this.getItemHandlerForContainer(), i, box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
                        ++i;
                    }
                }
                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        6, 3,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.ORANGE));

                return pieces;
            }
        });
        this.addInventoryToStorage(outItems, "crop_recolector_out");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(this.getFacing().getOpposite(), getRadius() + 1);
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).offset(corner1).expand(getRadius(), 0, getRadius());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;
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
                    this.world.setBlockToAir(blockPos.get(pointer));
                }

            } else if (BlockUtils.isLog(this.world, pos)) {
                doRecursiveChopping(world, pos, new ArrayList<BlockPos>(), new ArrayList<BlockPos>());
                needPointerIncrease = false;
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


    @Override
    protected int getEnergyForWorkRate() {
        return 20;
    }

    @Override
    protected int getMinimumWorkTicks() {
        return 10;
    }

    @Override
    protected int getEnergyForWork() {
        return 100;
    }

    @Override
    public long getWorkEnergyCapacity() {
        return 100;
    }

    public void doRecursiveChopping(World world, BlockPos current, List<BlockPos> blocksChecked, List<BlockPos> blocksChopped) {
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
        if (blocksChopped.size() > 25) return;
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
    }
}
