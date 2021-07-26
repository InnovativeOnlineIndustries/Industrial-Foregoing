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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import com.buuz135.industrial.block.tile.IndustrialWorkingTile.WorkAction;

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
				.setValidator(fluidStack -> fluidStack.getFluid().is(IndustrialTags.Fluids.EXPERIENCE))
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
		LivingEntity entity = (LivingEntity) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.level, MobDuplicatorConfig.exactCopy && exactCopy);
		if (entity == null) return new WorkAction(1, 0);

		List<LivingEntity> entityAmount = level.getEntitiesOfClass(entity.getClass(), getWorkingArea().bounds());
		entityAmount.removeIf(entityLiving -> !entityLiving.isAlive());
		if (entityAmount.size() > 32) return new WorkAction(1, 0);

		int essenceNeeded = (int) (entity.getHealth() * MobDuplicatorConfig.essenceNeeded);
		int canSpawn = (int) ((tank.getFluid().isEmpty() ? 0 : tank.getFluid().getAmount()) / Math.max(essenceNeeded, 1));
		if (canSpawn == 0) return new WorkAction(1, 0);

		int spawnAmount = 1 + this.level.random.nextInt(Math.min(canSpawn, 4));
		List<BlockPos> blocks = BlockUtils.getBlockPosInAABB(getWorkingArea().bounds());
		while (spawnAmount > 0) {
			if (tank.getFluid().getAmount() >= essenceNeeded) {
				entity = (LivingEntity) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.level, MobDuplicatorConfig.exactCopy && exactCopy);
				int tries = 20;
				Vec3 random = blockPosToVec3d(blocks.get(this.level.random.nextInt(blocks.size())));
				random = random.add(0.5, 0, 0.5);
				entity.moveTo(random.x, random.y, random.z, level.random.nextFloat() * 360F, 0);
				entity.setUUID(UUID.randomUUID());
				if (entity instanceof Mob){
					((Mob) entity).finalizeSpawn((ServerLevelAccessor) this.level, this.level.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.MOB_SUMMONED, null, null);
				}
				while (tries > 0 && !canEntitySpawn(entity)) {
					random = blockPosToVec3d(blocks.get(this.level.random.nextInt(blocks.size())));
					random = random.add(0.5, 0, 0.5);
					entity.moveTo(random.x, random.y, random.z, level.random.nextFloat() * 360F, 0);
					--tries;
				}
				if (tries <= 0) {
					--spawnAmount;
					continue;
				}

				this.level.addFreshEntity(entity);

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
		return this.level.isUnobstructed(living) && (!this.level.containsAnyLiquid(living.getBoundingBox()));
	}

	private Vec3 blockPosToVec3d(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

	@Override
	public VoxelShape getWorkingArea() {
		return new RangeManager(this.worldPosition, this.getFacingDirection(), RangeManager.RangeType.TOP_UP) {
			@Override
			public AABB getBox() {
				return super.getBox().expandTowards(0, 1, 0);
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
