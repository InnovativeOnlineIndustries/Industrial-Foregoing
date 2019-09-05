package com.buuz135.industrial.block.core.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.inventory.SidedInvHandler;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class LatexProcessingUnitTile extends IndustrialProcessingTile {

    private static int AMOUNT_LATEX = 100;
    private static int AMOUNT_WATER = 500;

    @Save
    private SidedFluidTank latex;
    @Save
    private SidedFluidTank water;
    @Save
    private SidedInvHandler output;

    public LatexProcessingUnitTile() {
        super(ModuleCore.LATEX_PROCESSING, 48 + 25, 40, 100);
        this.addTank(latex = (SidedFluidTank) new SidedFluidTank("latex", 16000, 29, 20, 1).
                setColor(DyeColor.GRAY).
                setTile(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.LATEX.getSourceFluid())));
        this.addTank(water = (SidedFluidTank) new SidedFluidTank("latex", 16000, 30 + 18, 20, 2).
                setColor(DyeColor.BLUE).
                setTile(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER)));
        this.addInventory(output = (SidedInvHandler) new SidedInvHandler("output", 70 + 34, 22, 6, 3).
                setColor(DyeColor.ORANGE).
                setRange(2, 3).
                setTile(this));
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
    protected int getTickPower() {
        return 20;
    }
}
