package com.buuz135.industrial.tile.mob;

import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.CustomOrientedBlock;
import com.buuz135.industrial.tile.block.MobRelocatorBlock;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;

import java.util.List;

public class MobRelocatorTile extends WorkingAreaElectricMachine {

    private IFluidTank outExp;
    private ItemStackHandler outItems;

    public MobRelocatorTile() {
        super(MobRelocatorTile.class.getName().hashCode(), 2, 1);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outExp = this.addFluidTank(FluidsRegistry.ESSENCE, 8000, EnumDyeColor.LIME, "Experience tank", new BoundingRectangle(50, 25, 18, 54));
        outItems = new ItemStackHandler(12){
            @Override
            protected void onContentsChanged(int slot) {
                MobRelocatorTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Mob drops",18 * 5 + 3, 25, 4,  3) {
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
    protected float performWork() {
        if (((CustomOrientedBlock)this.getBlockType()).isWorkDisabled()) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        if (mobs.size() == 0) return 0;
        EntityLiving mob = mobs.get(this.getWorld().rand.nextInt(mobs.size()));
        this.outExp.fill(new FluidStack(FluidsRegistry.ESSENCE, (int) (mob.getHealth()* ((MobRelocatorBlock) this.getBlockType()).getEssenceMultiplier())), true);
        mob.attackEntityFrom(DamageSource.GENERIC, mob.getHealth());
        List<EntityItem> items = this.getWorld().getEntitiesWithinAABB(EntityItem.class, area);
        for (EntityItem item : items) {
            if (!item.getEntityItem().isEmpty()) {
                ItemHandlerHelper.insertItem(outItems, item.getEntityItem(), false);
                item.setDead();
            }
        }

        return 1;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1).offset(EnumFacing.UP, getHeight());
        return this.getBlockType().getSelectedBoundingBox(this.world.getBlockState(this.pos), this.world, this.pos).expand(getRadius(), getHeight(), getRadius()).offset(corner1);
    }


}
