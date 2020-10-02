package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.core.LatexProcessingUnitConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class LatexProcessingUnitTile extends IndustrialProcessingTile<LatexProcessingUnitTile> {

    private static int AMOUNT_LATEX = 100;
    private static int AMOUNT_WATER = 500;

    @Save
    private SidedFluidTankComponent<LatexProcessingUnitTile> latex;
    @Save
    private SidedFluidTankComponent<LatexProcessingUnitTile> water;
    @Save
    private SidedInventoryComponent<LatexProcessingUnitTile> output;

    public LatexProcessingUnitTile() {
        super(ModuleCore.LATEX_PROCESSING, 48 + 25, 40);
        this.addTank(latex = (SidedFluidTankComponent<LatexProcessingUnitTile>) new SidedFluidTankComponent<LatexProcessingUnitTile>("latex", LatexProcessingUnitConfig.maxLatexTankSize, 29, 20, 0).
                setColor(DyeColor.LIGHT_GRAY).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.LATEX.getSourceFluid())));
        this.addTank(water = (SidedFluidTankComponent<LatexProcessingUnitTile>) new SidedFluidTankComponent<LatexProcessingUnitTile>("water", LatexProcessingUnitConfig.maxWaterTankSize, 30 + 18, 20, 1).
                setColor(DyeColor.BLUE).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER)));
        this.addInventory(output = (SidedInventoryComponent<LatexProcessingUnitTile>) new SidedInventoryComponent<LatexProcessingUnitTile>("output", 70 + 34, 22, 9, 2).
                setColor(DyeColor.ORANGE).
                setRange(3, 3).
                setComponentHarness(this));
    }

    @Override
    public boolean canIncrease() {
        return latex.getFluidAmount() >= AMOUNT_LATEX && water.getFluidAmount() >= AMOUNT_WATER && ItemHandlerHelper.insertItem(output, new ItemStack(ModuleCore.TINY_DRY_RUBBER), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            latex.drain(AMOUNT_LATEX, IFluidHandler.FluidAction.EXECUTE);
            water.drain(AMOUNT_WATER, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerHelper.insertItem(output, new ItemStack(ModuleCore.TINY_DRY_RUBBER), false);
        };
    }

    @Override
    protected EnergyStorageComponent<LatexProcessingUnitTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(LatexProcessingUnitConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return LatexProcessingUnitConfig.powerPerTick;
    }

    @Nonnull
    @Override
    public LatexProcessingUnitTile getSelf() {
        return this;
    }
}
