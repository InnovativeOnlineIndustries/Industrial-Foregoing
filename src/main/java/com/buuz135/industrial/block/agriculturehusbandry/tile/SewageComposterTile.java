package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IItemStackQuery;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.PosInvHandler;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SewageComposterTile extends IndustrialProcessingTile {

    @Save
    public PosFluidTank sewage;
    @Save
    public PosInvHandler fertilizerOutput;

    public SewageComposterTile() {
        super(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER, 57, 40);
        this.addTank(sewage = new SidedFluidTank("sewage", 8000, 30, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(PosFluidTank.Action.FILL).
                setTile(this));
        this.addInventory(fertilizerOutput = new SidedInvHandler("fertilizerOutput", 90, 22, 12, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter(IItemStackQuery.NOTHING.toSlotFilter()).
                setRange(4, 3).
                setTile(this));
    }

    @Override
    public boolean canIncrease() {
        return sewage.getFluidAmount() >= 1000 && ItemHandlerHelper.insertItem(fertilizerOutput, new ItemStack(ModuleCore.FERTILIZER), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            sewage.drainForced(1000, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerHelper.insertItem(fertilizerOutput, new ItemStack(ModuleCore.FERTILIZER), false);
        };
    }

    @Override
    protected int getTickPower() {
        return 30;
    }
}
