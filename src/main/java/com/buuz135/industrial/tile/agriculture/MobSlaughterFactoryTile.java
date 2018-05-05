package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.item.addon.AdultFilterAddonItem;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.api.IAcceptsAdultFilter;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.List;

public class MobSlaughterFactoryTile extends WorkingAreaElectricMachine implements IAcceptsAdultFilter {

    private IFluidTank outMeat;
    private IFluidTank outPink;

    public MobSlaughterFactoryTile() {
        super(MobSlaughterFactoryTile.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outMeat = this.addFluidTank(FluidsRegistry.MEAT, 8000, EnumDyeColor.BROWN, "Meat tank", new BoundingRectangle(46, 25, 18, 54));
        outPink = this.addSimpleFluidTank(8000, "Pink Slime Tank", EnumDyeColor.PINK, 46 + 20, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        mobs.removeIf(entityLiving -> entityLiving.isDead);
        if (hasAddon()) mobs.removeIf(entityLiving -> entityLiving instanceof EntityAgeable && entityLiving.isChild());
        if (mobs.size() == 0) return 0;
        EntityLiving mob = mobs.get(this.getWorld().rand.nextInt(mobs.size()));
        this.outMeat.fill(new FluidStack(FluidsRegistry.MEAT, (int) (mob.getHealth() * BlockRegistry.mobSlaughterFactoryBlock.getMeatValue())), true);
        this.outPink.fill(new FluidStack(FluidsRegistry.PINK_SLIME, (int) mob.getHealth()), true);
        mob.setDropItemsWhenDead(false);
        mob.attackEntityFrom(CommonProxy.custom, mob.getMaxHealth());

        return 1;
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, outMeat);
    }

    @Override
    public boolean hasAddon() {
        return this.hasAddon(AdultFilterAddonItem.class);
    }
}
