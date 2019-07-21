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
package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.ArrayList;
import java.util.List;

public class PlantInteractorTile extends WorkingAreaElectricMachine {

    public static final List<PlantInteractorTile> WORKING_TILES = new ArrayList<>();

    private static String NBT_POINTER = "pointer";

    private IFluidTank sludge;
    private ItemStackHandler outItems;
    private int pointer;
    private boolean hasWorked;

    public PlantInteractorTile() {
        super(PlantInteractorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        sludge = this.addSimpleFluidTank(8000, "Sludge Tank", EnumDyeColor.BLACK, 50, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
        outItems = (ItemStackHandler) this.addSimpleInventory(12, "output_items", EnumDyeColor.ORANGE, "output_items", new BoundingRectangle(18 * 5 + 3, 25, 18 * 4, 18 * 3), (stack, integer) -> false, (stack, integer) -> true, false, null);
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (ItemStackUtils.isInventoryFull(outItems)) return 0;
        hasWorked = false;
        List<BlockPos> blockPos = BlockUtils.getBlockPosInAABB(getWorkingArea());
        if (pointer >= blockPos.size() / (BlockRegistry.plantInteractorBlock.getHeight() + 1)) pointer = 0;
        if (pointer < blockPos.size()) {
            BlockPos pointerPos = blockPos.get(pointer);
            for (int i = 0; i < BlockRegistry.plantInteractorBlock.getHeight() + 1; ++i) {
                BlockPos tempPos = new BlockPos(pointerPos.getX(), pointerPos.getY() + i, pointerPos.getZ());
                if (!BlockUtils.canBlockBeBroken(this.world, tempPos)) continue;
                IBlockState tempState = this.world.getBlockState(tempPos);
                if (tempState.getBlock() instanceof IPlantable || tempState.getBlock() instanceof IGrowable) {
                    FakePlayer player = IndustrialForegoing.getFakePlayer(this.world, tempPos.up());
                    player.inventory.clear();
                    WORKING_TILES.add(this);
                    tempState.getBlock().onBlockActivated(this.world, tempPos, tempState, player, EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
                    ForgeHooks.onRightClickBlock(player, EnumHand.MAIN_HAND, tempPos, EnumFacing.UP, new Vec3d(0, 0, 0));
                    for (ItemStack stack : player.inventory.mainInventory) {
                        if (!stack.isEmpty()) {
                            ItemHandlerHelper.insertItem(outItems, stack, false);
                            sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, 10 * stack.getCount()), true);
                            hasWorked = true;
                        }
                    }
                    player.inventory.clear();
                    WORKING_TILES.remove(this);
                }
            }
        }
        ++pointer;
        return hasWorked ? 1 : 0.1f;
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
        pointer = compound.getInteger(NBT_POINTER);
    }

    public void harvestDrops(BlockEvent.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            ItemHandlerHelper.insertItem(outItems, stack, false);
            sludge.fill(new FluidStack(FluidsRegistry.SLUDGE, 10 * stack.getCount()), true);
        }
        event.getDrops().clear();
        hasWorked = true;
    }
}
