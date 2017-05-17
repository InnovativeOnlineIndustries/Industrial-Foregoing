package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.item.MobImprisonmentToolItem;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class MobDuplicatorTile extends WorkingAreaElectricMachine {

    private IFluidTank experienceTank;
    private ItemStackHandler mobTool;

    public MobDuplicatorTile() {
        super(MobDuplicatorTile.class.getName().hashCode(), 4, 1, false);
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
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).expand(getRadius(), getHeight(), getRadius());
    }

    @Override
    protected float performWork() {
        if (((CustomOrientedBlock) this.getBlockType()).isWorkDisabled()) return 0;
        if (mobTool.getStackInSlot(0).isEmpty()) return 0;
        if (experienceTank.getFluid() == null) return 0;
        AxisAlignedBB alignedBB = getWorkingArea();
        List<EntityLiving> livings = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, alignedBB);
        if (livings.size() > 20) return 0;
        ItemStack stack = mobTool.getStackInSlot(0);
        EntityLiving entity = (EntityLiving) ((MobImprisonmentToolItem) stack.getItem()).getEntitFromStack(stack, this.world, false);
        int canSpawn = (int) ((experienceTank.getFluid() == null ? 0 : experienceTank.getFluid().amount) / (entity.getHealth() * 2));
        if (canSpawn == 0) return 0;
        int spawnAmount = 1 + this.world.rand.nextInt(Math.min(canSpawn, 4));
        List<BlockPos> blocks = BlockUtils.getBlockPosInAABB(alignedBB);
        while (spawnAmount > 0) {
            if (experienceTank.getFluid() != null && experienceTank.getFluid().amount > entity.getHealth() * 2) {
                int tries = 20;
                BlockPos random = blocks.get(this.world.rand.nextInt(blocks.size()));
                while (tries > 0 && !this.world.isAirBlock(random)) {
                    random = blocks.get(this.world.rand.nextInt(blocks.size()));
                    --tries;
                }
                entity = (EntityLiving) ((MobImprisonmentToolItem) stack.getItem()).getEntitFromStack(stack, this.world, false);
                entity.setPosition(random.getX() + 0.5, random.getY(), random.getZ() + 0.5);
                this.world.spawnEntity(entity);
                experienceTank.drain((int) (entity.getHealth() * 2), true);
            }
            --spawnAmount;
        }
        return 1;
    }
}
