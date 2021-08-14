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

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.capability.tile.BigEnergyHandler;
import com.buuz135.industrial.config.machine.agriculturehusbandry.MobDuplicatorConfig;
import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.item.addon.RangeAddonItem;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleTool;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.item.AugmentWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class MobDuplicatorTile extends IndustrialAreaWorkingTile<MobDuplicatorTile> {

	private int maxProgress;
	private int powerPerOperation;
	private boolean exactCopy;

	@Save
	private SidedFluidTankComponent<MobDuplicatorTile> tank;

	@Save
	private SidedInventoryComponent<MobDuplicatorTile> input;

	public MobDuplicatorTile() {
		super(ModuleAgricultureHusbandry.MOB_DUPLICATOR, RangeManager.RangeType.TOP_UP, true, MobDuplicatorConfig.powerPerOperation);
		this.addTank(tank = (SidedFluidTankComponent<MobDuplicatorTile>) new SidedFluidTankComponent<MobDuplicatorTile>("essence", MobDuplicatorConfig.tankSize, 43, 20, 0)
				.setColor(DyeColor.LIME)
				.setTankAction(FluidTankComponent.Action.FILL)
				.setComponentHarness(this)
				.setValidator(fluidStack -> fluidStack.getFluid().isIn(IndustrialTags.Fluids.EXPERIENCE))
		);

		this.addInventory(input = (SidedInventoryComponent<MobDuplicatorTile>) new SidedInventoryComponent<MobDuplicatorTile>("mob_imprisonment_tool", 64, 22, 1, 1)
				.setColor(DyeColor.ORANGE)
				.setInputFilter((itemStack, integer) -> itemStack.getItem().equals(ModuleTool.MOB_IMPRISONMENT_TOOL))
				.setComponentHarness(this)
		);

		this.maxProgress = MobDuplicatorConfig.maxProgress;
		this.powerPerOperation = MobDuplicatorConfig.powerPerOperation;
		this.exactCopy = MobDuplicatorConfig.exactCopy;
	}

	@Override
	public WorkAction work() {

		if (input.getStackInSlot(0).isEmpty() || !hasEnergy(MobDuplicatorConfig.powerPerOperation)) return new WorkAction(1, 0);
		if (tank.getFluid().isEmpty()) return new WorkAction(1, 0);

		ItemStack stack = input.getStackInSlot(0);
		LivingEntity entity = (LivingEntity) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.world, MobDuplicatorConfig.exactCopy && exactCopy);
		if (entity == null) return new WorkAction(1, 0);

		List<LivingEntity> entityAmount = world.getEntitiesWithinAABB(entity.getClass(), getWorkingArea().getBoundingBox());
		entityAmount.removeIf(entityLiving -> !entityLiving.isAlive());
		if (entityAmount.size() > 32) return new WorkAction(1, 0);

		int essenceNeeded = (int) (entity.getHealth() * MobDuplicatorConfig.essenceNeeded);
		int canSpawn = (int) ((tank.getFluid().isEmpty() ? 0 : tank.getFluid().getAmount()) / Math.max(essenceNeeded, 1));
		if (canSpawn == 0) return new WorkAction(1, 0);

		int spawnAmount = 1 + this.world.rand.nextInt(Math.min(canSpawn, 4));
		List<BlockPos> blocks = BlockUtils.getBlockPosInAABB(getWorkingArea().getBoundingBox());
		while (spawnAmount > 0) {
			if (tank.getFluid().getAmount() >= essenceNeeded) {
				entity = (LivingEntity) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.world, MobDuplicatorConfig.exactCopy && exactCopy);
				int tries = 20;
				Vector3d random = blockPosToVec3d(blocks.get(this.world.rand.nextInt(blocks.size())));
				random = random.add(0.5, 0, 0.5);
				entity.setLocationAndAngles(random.x, random.y, random.z, world.rand.nextFloat() * 360F, 0);
				entity.setUniqueId(UUID.randomUUID());
				if (entity instanceof MobEntity){
					((MobEntity) entity).onInitialSpawn((IServerWorld) this.world, this.world.getDifficultyForLocation(this.pos), SpawnReason.MOB_SUMMONED, null, null);
				}
				while (tries > 0 && !canEntitySpawn(entity)) {
					random = blockPosToVec3d(blocks.get(this.world.rand.nextInt(blocks.size())));
					random = random.add(0.5, 0, 0.5);
					entity.setLocationAndAngles(random.x, random.y, random.z, world.rand.nextFloat() * 360F, 0);
					--tries;
				}
				if (tries <= 0) {
					--spawnAmount;
					continue;
				}

				this.world.addEntity(entity);

				tank.drainForced(essenceNeeded, IFluidHandler.FluidAction.EXECUTE);
			}
			--spawnAmount;
		}
		if (canSpawn > 0) {
			return new WorkAction(1f, MobDuplicatorConfig.powerPerOperation);
		}

		return new WorkAction(1, 0);
	}

	private boolean canEntitySpawn(LivingEntity living) {
		return this.world.checkNoEntityCollision(living) && (!this.world.containsAnyLiquid(living.getBoundingBox()));
	}

	private Vector3d blockPosToVec3d(BlockPos blockPos) {
		return new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	@Override
	public VoxelShape getWorkingArea() {
		return new RangeManager(this.pos, this.getFacingDirection(), RangeManager.RangeType.TOP_UP) {
			@Override
			public AxisAlignedBB getBox() {
				return super.getBox().expand(0, 1, 0);
			}
		}.get(hasAugmentInstalled(RangeAddonItem.RANGE) ? ((int) AugmentWrapper.getType(getInstalledAugments(RangeAddonItem.RANGE).get(0), RangeAddonItem.RANGE) + 1) : 0);
	}

	@Nonnull
	@Override
	public MobDuplicatorTile getSelf() {
		return this;
	}

	@Nonnull
	@Override
	protected EnergyStorageComponent<MobDuplicatorTile> createEnergyStorage() {
		return new BigEnergyHandler<MobDuplicatorTile>(MobDuplicatorConfig.maxStoredPower, 10, 20) {
			@Override
			public void sync() {
				MobDuplicatorTile.this.syncObject(getEnergyStorage());
			}
		};
	}

	@Override
	public int getMaxProgress() {
		return MobDuplicatorConfig.maxProgress;
	}
}
