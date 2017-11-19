package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;
import java.util.UUID;

public class MobDuplicatorTile extends WorkingAreaElectricMachine {

    private IFluidTank experienceTank;
    private ItemStackHandler mobTool;

    public MobDuplicatorTile() {
        super(MobDuplicatorTile.class.getName().hashCode(), 4, 2, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        this.experienceTank = this.addFluidTank(FluidsRegistry.ESSENCE, 8000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));
        mobTool = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                MobDuplicatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(mobTool, EnumDyeColor.ORANGE, "Mob imprisonment Tool", 18 * 5 + 3, 25, 1, 1) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return stack.getItem().equals(ItemRegistry.mobImprisonmentToolItem) && ((MobImprisonmentToolItem) stack.getItem()).containsEntity(stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(mobTool, "mob_replicator_tool");
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(getRadius(), getHeight(), getRadius());
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (mobTool.getStackInSlot(0).isEmpty()) return 0;
        if (experienceTank.getFluid() == null) return 0;

        ItemStack stack = mobTool.getStackInSlot(0);
        EntityLiving entity = (EntityLiving) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.world, false);

        int livingAround = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double) (pos.getX() + 1), (double) (pos.getY() + 1), (double) (pos.getZ() + 1))).grow((double) 16)).size();
        if (livingAround > 32) return 0;

        int canSpawn = (int) ((experienceTank.getFluid() == null ? 0 : experienceTank.getFluid().amount) / (entity.getHealth() * 4));
        if (canSpawn == 0) return 0;

        int spawnAmount = 1 + this.world.rand.nextInt(Math.min(canSpawn, 4));
        List<BlockPos> blocks = BlockUtils.getBlockPosInAABB(getWorkingArea());
        int essenceNeeded = (int) (entity.getHealth() * BlockRegistry.mobDuplicatorBlock.essenceNeeded);
        while (spawnAmount > 0) {
            if (experienceTank.getFluid() != null && experienceTank.getFluid().amount > essenceNeeded) {
                entity = (EntityLiving) ((MobImprisonmentToolItem) stack.getItem()).getEntityFromStack(stack, this.world, false);
                int tries = 20;
                BlockPos random = blocks.get(this.world.rand.nextInt(blocks.size())).add(0.5, 0, 0.5);
                entity.setUniqueId(UUID.randomUUID());
                entity.setLocationAndAngles(random.getX(), random.getY(), random.getZ(), world.rand.nextFloat() * 360F, 0);
                while (tries > 0 && !canEntitySpawn(entity)) {
                    random = blocks.get(this.world.rand.nextInt(blocks.size()));
                    entity.setLocationAndAngles(random.getX(), random.getY(), random.getZ(), world.rand.nextFloat() * 360F, 0);
                    --tries;
                }

                if (tries <= 0) {
                    --spawnAmount;
                    continue;
                }
                entity.onInitialSpawn(world.getDifficultyForLocation(this.pos), null);

                this.world.spawnEntity(entity);

                if (entity != null) entity.spawnExplosionParticle();
                experienceTank.drain(essenceNeeded, true);
            }
            --spawnAmount;
        }

        return 1;
    }

    private boolean canEntitySpawn(EntityLiving living) {
        return getWorkingArea().contains(new Vec3d(living.getEntityBoundingBox().minX, living.getEntityBoundingBox().minY, living.getEntityBoundingBox().minZ)) &&
                getWorkingArea().contains(new Vec3d(living.getEntityBoundingBox().maxX, living.getEntityBoundingBox().maxY, living.getEntityBoundingBox().maxZ))
                && this.world.checkNoEntityCollision(living.getEntityBoundingBox()) && this.world.getCollisionBoxes(living, living.getEntityBoundingBox()).isEmpty() && (!this.world.containsAnyLiquid(living.getEntityBoundingBox()) || living.isCreatureType(EnumCreatureType.WATER_CREATURE, false));
    }
}
