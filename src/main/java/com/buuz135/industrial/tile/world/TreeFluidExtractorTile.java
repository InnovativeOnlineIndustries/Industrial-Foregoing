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

import com.buuz135.industrial.api.extractor.ExtractorEntry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomSidedTileEntity;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import lombok.Data;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.ArrayList;
import java.util.List;

public class TreeFluidExtractorTile extends CustomSidedTileEntity {

    private IFluidTank tank;
    private int id;

    public TreeFluidExtractorTile() {
        super(TreeFluidExtractorTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(FluidsRegistry.LATEX, 8000, EnumDyeColor.GRAY, "Latex tank", new BoundingRectangle(17, 25, 18, 54));
    }

    @Override
    protected boolean supportsAddons() {
        return false;
    }

    @Override
    protected void innerUpdate() {
        if (WorkUtils.isDisabled(this.getBlockType())) return;
        if (this.getWorld().isRemote) return;
        if (tank.getFluidAmount() == 8000) return;
        if (world.getTotalWorldTime() % 5 == 0) {
            ExtractorEntry extractorEntry = ExtractorEntry.getExtractorEntry(this.world, this.pos.offset(this.getFacing().getOpposite()));
            if (extractorEntry != null) {
                WoodLodProgress woodLog = WoodLodProgress.getWoodLogOrDefault(this.world, this.pos.offset(this.getFacing().getOpposite()));
                tank.fill(extractorEntry.getFluidStack(), true);
                if (id == 0) id = this.world.rand.nextInt();
                if (world.rand.nextDouble() <= extractorEntry.getBreakChance())
                    woodLog.setProgress(woodLog.getProgress() + 1);
                if (woodLog.getProgress() > 7) {
                    woodLog.setProgress(0);
                    this.world.setBlockToAir(this.pos.offset(this.getFacing().getOpposite()));
                }
            }
        }
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.fillItemFromTank(fluidItems, tank);
    }

    @Override
    public boolean getAllowRedstoneControl() {
        return false;
    }

    @Override
    protected boolean getShowPauseDrawerPiece() {
        return false;
    }

    public @Data
    static class WoodLodProgress {

        public static List<WoodLodProgress> woodLodProgressList = new ArrayList<>();

        private World world;
        private BlockPos blockPos;
        private int progress;
        private int breakID;

        public WoodLodProgress(World world, BlockPos blockPos) {
            this.world = world;
            this.blockPos = blockPos;
            this.progress = 0;
            this.breakID = world.rand.nextInt();
            woodLodProgressList.add(this);
        }

        public static WoodLodProgress getWoodLogOrDefault(World world, BlockPos pos) {
            for (WoodLodProgress progress : woodLodProgressList) {
                if (progress.getBlockPos().equals(pos) && progress.getWorld().equals(world)) return progress;
            }
            return new WoodLodProgress(world, pos);
        }

        public static void remove(World world, BlockPos pos) {
            woodLodProgressList.remove(getWoodLogOrDefault(world, pos));
        }
    }
}
