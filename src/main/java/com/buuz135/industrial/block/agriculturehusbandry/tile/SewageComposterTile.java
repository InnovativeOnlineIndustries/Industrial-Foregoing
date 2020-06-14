package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.agriculturehusbandry.SewageComposterConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SewageComposterTile extends IndustrialProcessingTile<SewageComposterTile> {

    private int maxProgress;
    private int powerPerTick;

    @Save
    public SidedFluidTankComponent<SewageComposterTile> sewage;
    @Save
    public SidedInventoryComponent<SewageComposterTile> fertilizerOutput;

    public SewageComposterTile() {
        super(ModuleAgricultureHusbandry.SEWAGE_COMPOSTER, 57, 40);
        this.addTank(sewage = (SidedFluidTankComponent<SewageComposterTile>) new SidedFluidTankComponent<SewageComposterTile>("sewage", SewageComposterConfig.maxTankSize, 30, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.SEWAGE.getSourceFluid())));
        this.addInventory(fertilizerOutput = (SidedInventoryComponent<SewageComposterTile>) new SidedInventoryComponent<SewageComposterTile>("fertilizer", 90, 22, 12, 1).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setRange(4, 3).
                setComponentHarness(this));
        this.maxProgress = SewageComposterConfig.maxProgress;
        this.powerPerTick = SewageComposterConfig.powerPerTick;
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
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, SewageComposterConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    protected int getTickPower() {
        return powerPerTick;
    }

    @Nonnull
    @Override
    public SewageComposterTile getSelf() {
        return this;
    }
}
