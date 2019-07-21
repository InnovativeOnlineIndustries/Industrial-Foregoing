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
package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.FluidTankType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FluidPumpTile extends WorkingAreaElectricMachine {

    private static final String NBT_FLUID = "Fluid";
    private static final String NBT_WORK = "Work";

    private IFluidTank tank;
    private String fluid;
    private Queue<BlockPos> allBlocks;
    private boolean isWorkFinished;
    private int lowestY;

    public FluidPumpTile() {
        super(FluidPumpTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(32000, "output tank", EnumDyeColor.ORANGE, 50, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
        isWorkFinished = false;
        lowestY = 256;
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        if (fluid == null && FluidRegistry.lookupFluidForBlock(this.world.getBlockState(this.pos.add(0, -1, 0)).getBlock()) != null)
            fluid = FluidRegistry.lookupFluidForBlock(this.world.getBlockState(this.pos.add(0, -1, 0)).getBlock()).getName();
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType()) || isWorkFinished || fluid == null) return 0;
        if (tank.getFluidAmount() + 1000 <= tank.getCapacity()) {
            if (lowestY == this.pos.getY()) {
                isWorkFinished = true;
                allBlocks = null;
                return 0;
            }
            if (allBlocks == null || allBlocks.isEmpty()) {
                fillCache();
            }
            BlockPos peeked = allBlocks.peek();
            while (!allBlocks.isEmpty() && (this.world.isOutsideBuildHeight(peeked) || !isBlockSameFluid(peeked) || this.world.getBlockState(peeked).getBlock().getMetaFromState(this.world.getBlockState(peeked)) != 0)) {
                allBlocks.poll();
                if (!allBlocks.isEmpty()) peeked = allBlocks.peek();
            }
            if (peeked == null) return 0;
            if (this.world.getTileEntity(peeked) != null) return 0;
            IFluidHandler handler = FluidUtil.getFluidHandler(this.world, peeked, null);
            FluidStack fluid = handler.drain(1000, true);
            if (fluid != null) {
                if (BlockRegistry.fluidPumpBlock.isReplaceFluidWithCobble()) {
                    if (this.world.setBlockState(peeked, Blocks.COBBLESTONE.getDefaultState())) {
                        tank.fill(fluid, true);
                    }
                } else if (world.setBlockToAir(peeked)) {
                    tank.fill(fluid, true);
                }
            } else {
                return 0;
            }
            allBlocks.poll();
            return 1;
        }
        return 0;
    }

    private void fillCache() {
        int lowerY = this.getPos().getY();
        for (BlockPos blockPos : BlockUtils.getBlockPosInAABB(getWorkingArea())) {
            BlockPos checking = blockPos;
            while (isBlockSameFluid(checking)) {
                checking = checking.down();
            }
            if (checking.getY() < lowerY) {
                lowerY = checking.getY();
            }
        }
        this.lowestY = lowerY + 1;
        if (allBlocks == null) {
            allBlocks = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distanceSqToCenter(this.pos.getX(), lowestY, this.pos.getZ())).reversed());
        }
        allBlocks.addAll(BlockUtils.getBlockPosInAABB(getWorkingArea().offset(0, lowestY - this.getPos().getY() + 1, 0)));
        allBlocks.removeIf(pos1 -> !isBlockSameFluid(pos1));
    }

    private boolean isBlockSameFluid(BlockPos pos) {
        Fluid fluid = FluidRegistry.lookupFluidForBlock(this.world.getBlockState(pos).getBlock());
        return fluid != null && fluid.getName().equals(this.fluid);
    }

    //TODO make it fill buckets
    @Override
    public AxisAlignedBB getWorkingArea() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(0, -1, 0).grow((Math.ceil((getRadius() * getRadius()) / 2F)), 0, Math.ceil((getRadius() * getRadius()) / 2F));
    }

    @NotNull
    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        NBTTagCompound nbtTagCompound = super.writeToNBT(compound);
        nbtTagCompound.setBoolean(NBT_WORK, isWorkFinished);
        if (fluid != null) nbtTagCompound.setString(NBT_FLUID, fluid);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        fluid = null;
        isWorkFinished = compound.getBoolean(NBT_WORK);
        if (compound.hasKey(NBT_FLUID)) fluid = compound.getString(NBT_FLUID);
        if (fluid != null && !FluidRegistry.isFluidRegistered(fluid)) fluid = null;
        super.readFromNBT(compound);
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);
        pieces.add(new BasicRenderedGuiPiece(95, 22, 54, 20, null, 0, 0) {
            @Override
            public void drawForegroundLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY);
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.getLeft() + 2, this.getTop() + 8, 0);
                GlStateManager.scale(1, 1, 1);
                FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
                ItemStackUtils.renderItemIntoGUI(fluid == null ? new ItemStack(Items.BUCKET) : FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.getFluid(fluid), 1000)), 34, -5, 7);
                renderer.drawString(TextFormatting.DARK_GRAY + new TextComponentTranslation("text.industrialforegoing.display.filter").getUnformattedText(), 0, 0, 0xFFFFFF);
                GlStateManager.popMatrix();
            }

            @Override
            public void drawForegroundTopLayer(@NotNull BasicTeslaGuiContainer<?> container, int guiX, int guiY, int mouseX, int mouseY) {
                super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY);
                if (isInside(container, mouseX, mouseY))
                    container.drawTooltip(Arrays.asList(fluid != null ? FluidRegistry.getFluid(fluid).getLocalizedName(new FluidStack(FluidRegistry.getFluid(fluid), 1000)) : new TextComponentTranslation("text.industrialforegoing.display.empty").getUnformattedText()), mouseX - guiX, mouseY - guiY);
            }
        });
        return pieces;
    }

    @Override
    public int getActionsWork() {
        return 1;
    }
}
