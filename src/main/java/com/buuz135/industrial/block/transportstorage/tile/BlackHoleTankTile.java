package com.buuz135.industrial.block.transportstorage.tile;

import com.buuz135.industrial.utils.BlockUtils;
import com.buuz135.industrial.utils.NumberUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

public class BlackHoleTankTile extends BHTile<BlackHoleTankTile> {

    @Save
    public SidedFluidTankComponent<BlackHoleTankTile> tank;
    @Save
    private ItemStackFilter filter;
    private boolean isEmpty;

    public BlackHoleTankTile(BasicTileBlock<BlackHoleTankTile> base, Rarity rarity) {
        super(base);
        this.addTank(this.tank = (SidedFluidTankComponent<BlackHoleTankTile>) new SidedFluidTankComponent<>("tank", BlockUtils.getFluidAmountByRarity(rarity), 20, 20, 0)
                .setColor(DyeColor.BLUE)
                .setOnContentChange(() -> {
                    syncObject(tank);
                })
                .setValidator(fluidStack -> {
            if (!filter.getFilterSlots()[0].getFilter().isEmpty()){
                ItemStack stack = filter.getFilterSlots()[0].getFilter();
                IFluidHandlerItem iFluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElseGet(null);
                return iFluidHandlerItem != null && (iFluidHandlerItem.getFluidInTank(0).isFluidEqual(fluidStack));
            }
            return true;
        }));
        this.addFilter(filter = new ItemStackFilter("filter" , 1));
        FilterSlot slot = new FilterSlot<>(79, 60 , 0, ItemStack.EMPTY);
        slot.setColor(DyeColor.CYAN);
        this.filter.setFilter(0, slot);
        this.isEmpty = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (isEmpty != (tank.getFluidAmount() == 0)){
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
        isEmpty = tank.getFluidAmount() == 0;
    }

    @Override
    public ActionResultType onActivated(PlayerEntity playerIn, Hand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
        }
        openGui(playerIn);
        return ActionResultType.SUCCESS;
    }

    @Override
    public ItemStack getDisplayStack() {
        return tank.getFluidAmount() > 0 ? FluidUtil.getFilledBucket(tank.getFluid()) : new ItemStack(Items.BUCKET);
    }

    @Override
    public String getFormatedDisplayAmount() {
        return NumberUtils.getFormatedBigNumber(tank.getFluidAmount() / 1000) + " b";
    }

    @Nonnull
    @Override
    public BlackHoleTankTile getSelf() {
        return this;
    }

    public SidedFluidTankComponent<BlackHoleTankTile> getTank() {
        return tank;
    }
}
