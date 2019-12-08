package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import com.hrznstudio.titanium.block.tile.progress.PosProgressBar;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BiofuelGeneratorTile extends IndustrialGeneratorTile {

    @Save
    private SidedFluidTank biofuel;

    public BiofuelGeneratorTile() {
        super(ModuleGenerator.BIOFUEL_GENERATOR);
        addTank(biofuel = (SidedFluidTank) new SidedFluidTank("biofuel", 4000, 43, 20, 0).
                setColor(DyeColor.PURPLE).
                setTile(this).
                setTankAction(PosFluidTank.Action.FILL).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.BIOFUEL.getSourceFluid()))
        );
    }

    @Override
    public int consumeFuel() {
        if (biofuel.getFluidAmount() > 0) {
            biofuel.drainForced(1, IFluidHandler.FluidAction.EXECUTE);
            return 4;
        }
        return 0;
    }

    @Override
    public boolean canStart() {
        return biofuel.getFluidAmount() > 0;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 160;
    }

    @Override
    public PosProgressBar getProgressBar() {
        return new PosProgressBar(30, 20, 0, 100)
                .setTile(this)
                .setBarDirection(PosProgressBar.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return 1_000_000;
    }

    @Override
    public int getExtractingEnergy() {
        return 500;
    }
}
