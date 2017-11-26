package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.MobRelocatorBlock;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

import java.util.List;

public class MobRelocatorTile extends WorkingAreaElectricMachine {

    private IFluidTank outExp;
    private ItemStackHandler outItems;

    public MobRelocatorTile() {
        super(MobRelocatorTile.class.getName().hashCode(), 2, 1, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outExp = this.addFluidTank(FluidsRegistry.ESSENCE, 8000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));
        outItems = new ItemStackHandler(12) {
            @Override
            protected void onContentsChanged(int slot) {
                MobRelocatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Mob drops", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }

        });
        this.addInventoryToStorage(outItems, "mob_relocator_out");
    }

    @Override
    public void protectedUpdate() {
        super.protectedUpdate();
        this.getWorld().getEntitiesWithinAABB(EntityXPOrb.class, getWorkingArea().expand(2, 2, 2)).stream().filter(entityXPOrb -> !entityXPOrb.isDead).forEach(entityXPOrb -> {
            if (this.outExp.fill(new FluidStack(FluidsRegistry.ESSENCE, (int) (entityXPOrb.getXpValue() * 20 * ((MobRelocatorBlock) this.getBlockType()).getEssenceMultiplier())), false) > 0) {
                this.outExp.fill(new FluidStack(FluidsRegistry.ESSENCE, (int) (entityXPOrb.getXpValue() * 20 * ((MobRelocatorBlock) this.getBlockType()).getEssenceMultiplier())), true);
            }
            entityXPOrb.setDead();
        });
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        if (mobs.size() == 0) return 0;
        FakePlayer player = IndustrialForegoing.getFakePlayer(world);
        mobs.forEach(entityLiving -> entityLiving.attackEntityFrom(DamageSource.causePlayerDamage(player), Integer.MAX_VALUE));
        List<EntityItem> items = this.getWorld().getEntitiesWithinAABB(EntityItem.class, area);
        for (EntityItem item : items) {
            if (!item.getItem().isEmpty()) {
                ItemHandlerHelper.insertItem(outItems, item.getItem(), false);
                item.setDead();
            }
        }
        return 1;
    }


    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1).offset(EnumFacing.UP, getHeight());
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(getRadius(), getHeight(), getRadius()).offset(corner1);
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, outExp);
    }
}
