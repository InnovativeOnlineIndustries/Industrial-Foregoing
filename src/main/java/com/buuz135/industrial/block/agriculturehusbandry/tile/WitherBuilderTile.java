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
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class WitherBuilderTile extends IndustrialAreaWorkingTile<WitherBuilderTile> {

	@Save
	private SidedInventoryComponent<WitherBuilderTile> top;

	@Save
	private SidedInventoryComponent<WitherBuilderTile> middle;

	@Save
	private SidedInventoryComponent<WitherBuilderTile> bottom;

	public WitherBuilderTile() {
		super(ModuleAgricultureHusbandry.WITHER_BUILDER, RangeManager.RangeType.TOP_UP, true, WitherBuilderConfig.powerPerOperation);


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
		if (!hasEnergy(WitherBuilderConfig.powerPerOperation) || BlockUtils.canBlockBeBroken(this.world, this.pos))
			return new WorkAction(1, 0);
		BlockPos pos = this.pos.add(0, 2, 0);
		float power = 0;
		if (this.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
			this.world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
			getDefaultOrFind(0, bottom, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
			power += 1 / 7f;
		}
		if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
			for (int i = 0; i < 3; ++i) {
				BlockPos temp;
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					temp = pos.add(0, 1, i - 1);
				} else {
					temp = pos.add(i - 1, 1, 0);
				}
				if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).isEmpty()) {
					this.world.setBlockState(temp, Blocks.SOUL_SAND.getDefaultState());
					getDefaultOrFind(i, middle, new ItemStack(Blocks.SOUL_SAND)).shrink(1);
					power += 1 / 7f;
				}
			}
		}
		if (this.world.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND)) {
			boolean secondRow = true;
			for (int i = 0; i < 3; ++i) {
				BlockPos temp;
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					temp = pos.add(0, 1, i - 1);
				} else {
					temp = pos.add(i - 1, 1, 0);
				}
				if (!this.world.getBlockState(temp).getBlock().equals(Blocks.SOUL_SAND)) {
					secondRow = false;
					break;
				}
			}
			if (secondRow) {
				for (int i = 0; i < 3; ++i) {
					BlockPos temp;
					if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
						temp = pos.add(0, 2, i - 1);
					} else {
						temp = pos.add(i - 1, 2, 0);
					}
					if (this.world.getBlockState(temp).getBlock().equals(Blocks.AIR) && !getDefaultOrFind(i, top, new ItemStack(Items.WITHER_SKELETON_SKULL)).isEmpty() && this.world.getBlockState(temp.add(0, -1, 0)).getBlock().equals(Blocks.SOUL_SAND)) {
						IFFakePlayer fakePlayer = (IFFakePlayer) IndustrialForegoing.getFakePlayer(world, temp);
						ItemStack stack = getDefaultOrFind(i, top, new ItemStack(Items.WITHER_SKELETON_SKULL));
						if (fakePlayer.placeBlock(this.world, temp, stack)) {
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

		return new RangeManager(this.pos, this.getFacingDirection(), RangeManager.RangeType.TOP_UP) {
			@Override
			public AxisAlignedBB getBox() {
				if (getFacingDirection() == Direction.EAST || getFacingDirection() == Direction.WEST) {
					return super.getBox().offset(new BlockPos(0, 1, 0)).grow(0, 1, 1);
				} else {
					return super.getBox().offset(new BlockPos(0, 1, 0)).grow(1, 1, 0);
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
		if (handler.getStackInSlot(i).isItemEqual(filter)) return handler.getStackInSlot(i);
		for (ItemStackHandler h : new ItemStackHandler[] {top, middle, bottom}) {
			for (int s = 0; s < h.getSlots(); ++s) {
				if (h.getStackInSlot(s).isItemEqual(filter)) return h.getStackInSlot(s);
			}
		}
		return ItemStack.EMPTY;
	}
}
