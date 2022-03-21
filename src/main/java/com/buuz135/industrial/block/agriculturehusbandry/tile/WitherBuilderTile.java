/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.capability.tile.BigEnergyHandler;
import com.buuz135.industrial.config.machine.agriculturehusbandry.WitherBuilderConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IFFakePlayer;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile.WorkAction;

public class WitherBuilderTile extends IndustrialAreaWorkingTile<WitherBuilderTile> {

	@Save
	private SidedInventoryComponent<WitherBuilderTile> top;

	@Save
	private SidedInventoryComponent<WitherBuilderTile> middle;

	@Save
	private SidedInventoryComponent<WitherBuilderTile> bottom;

	public WitherBuilderTile(BlockPos blockPos, BlockState blockState) {
		super(ModuleAgricultureHusbandry.WITHER_BUILDER, RangeManager.RangeType.TOP_UP, true, WitherBuilderConfig.powerPerOperation, blockPos, blockState);
		this.addInventory(top = (SidedInventoryComponent<WitherBuilderTile>) new SidedInventoryComponent<WitherBuilderTile>("wither_skulls", 64, 25, 3, 0)
				.setColor(DyeColor.BLACK)
				.setInputFilter((itemStack, integer) -> itemStack.getItem().equals(Items.WITHER_SKELETON_SKULL))
				.setSlotLimit(1)
				.setComponentHarness(this)
		);
		this.addInventory(middle = (SidedInventoryComponent<WitherBuilderTile>) new SidedInventoryComponent<WitherBuilderTile>("soulsand", 64, 25 + 18, 3, 1)
				.setColor(DyeColor.ORANGE)
				.setInputFilter((itemStack, integer) -> itemStack.getItem().equals(Blocks.SOUL_SAND.asItem()))
				.setSlotLimit(1)
				.setComponentHarness(this)
		);
		this.addInventory(bottom = (SidedInventoryComponent<WitherBuilderTile>) new SidedInventoryComponent<WitherBuilderTile>("soulsand", 64 + 18, 25 + 18 * 2, 1, 2)
				.setColor(DyeColor.ORANGE)
				.setInputFilter((itemStack, integer) -> itemStack.getItem().equals(Blocks.SOUL_SAND.asItem()))
				.setSlotLimit(1)
				.setComponentHarness(this)
		);
	}

	@Override
	public WorkAction work() {
		if (!hasEnergy(WitherBuilderConfig.powerPerOperation) || !BlockUtils.canBlockBeBroken(this.level, this.worldPosition))
			return new WorkAction(1, 0);
		BlockPos pos = this.worldPosition.offset(0, 2, 0);
		float power = 0;
		if (this.level.getBlockState(pos).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
			this.level.setBlockAndUpdate(pos, Blocks.SOUL_SAND.defaultBlockState());
			getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
			power += 1 / 7f;
		}
		if (this.level.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
			for (int i = 0; i < 3; ++i) {
				BlockPos temp;
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					temp = pos.offset(0, 1, i - 1);
				} else {
					temp = pos.offset(i - 1, 1, 0);
				}
				if (this.level.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
					this.level.setBlockAndUpdate(temp, Blocks.SOUL_SAND.defaultBlockState());
					getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
					power += 1 / 7f;
				}
			}
		}
		if (this.level.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
			boolean secondRow = true;
			for (int i = 0; i < 3; ++i) {
				BlockPos temp;
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					temp = pos.offset(0, 1, i - 1);
				} else {
					temp = pos.offset(i - 1, 1, 0);
				}
				if (!this.level.getBlockState(temp).getBlock().equals(Blocks.SOUL_SAND)) {
					secondRow = false;
					break;
				}
			}
			if (secondRow) {
				for (int i = 0; i < 3; ++i) {
					BlockPos temp;
					if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
						temp = pos.offset(0, 2, i - 1);
					} else {
						temp = pos.offset(i - 1, 2, 0);
					}
					if (this.level.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, top, new ItemStack(Items.WITHER_SKELETON_SKULL)).isEmpty() && this.level.getBlockState(temp.offset(0, -1, 0)).getBlock().equals(Blocks.SOUL_SAND)) {
						IFFakePlayer fakePlayer = (IFFakePlayer) IndustrialForegoing.getFakePlayer(level, temp);
						ItemStack stack = getDefaultOrFind(i, top, new ItemStack(Items.WITHER_SKELETON_SKULL));
						if (fakePlayer.placeBlock(this.level, temp, stack)) {
							power += 1 / 7f;
						}
					}
				}
			}
		}
		return new WorkAction(1, power > 0 ? WitherBuilderConfig.powerPerOperation : 0);
	}

	@Override
	public VoxelShape getWorkingArea() {

		return new RangeManager(this.worldPosition, this.getFacingDirection(), RangeManager.RangeType.TOP_UP) {
			@Override
			public AABB getBox() {
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					return super.getBox().move(new BlockPos(0, 1, 0)).inflate(0, 1, 1);
				} else {
					return super.getBox().move(new BlockPos(0, 1, 0)).inflate(1, 1, 0);
				}
			}
		}.get(0);

	}

	@Nonnull
	@Override
	public WitherBuilderTile getSelf() {
		return this;
	}

	@Nonnull
	@Override
	protected EnergyStorageComponent<WitherBuilderTile> createEnergyStorage() {
		return new BigEnergyHandler<WitherBuilderTile>(WitherBuilderConfig.maxStoredPower, 10, 20) {
			@Override
			public void sync() {
				WitherBuilderTile.this.syncObject(getEnergyStorage());
			}
		};
	}

	@Override
	public int getMaxProgress() {
		return WitherBuilderConfig.maxProgress;
	}


	public ItemStack getDefaultOrFind(int i, ItemStackHandler handler, ItemStack filter) {
		if (handler.getStackInSlot(i).sameItem(filter)) return handler.getStackInSlot(i);
		for (ItemStackHandler h : new ItemStackHandler[] {top, middle, bottom}) {
			for (int s = 0; s < h.getSlots(); ++s) {
				if (h.getStackInSlot(s).sameItem(filter)) return h.getStackInSlot(s);
			}
		}
		return ItemStack.EMPTY;
	}
}
