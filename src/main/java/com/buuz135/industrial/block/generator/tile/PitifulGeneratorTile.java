package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

public class PitifulGeneratorTile extends IndustrialGeneratorTile<PitifulGeneratorTile> {

    @Save
    private SidedInventoryComponent<PitifulGeneratorTile> fuel;

    public PitifulGeneratorTile() {
        super(ModuleGenerator.PITIFUL_GENERATOR);
        this.addInventory(fuel = (SidedInventoryComponent<PitifulGeneratorTile>) new SidedInventoryComponent<PitifulGeneratorTile>("fuel_input", 46, 22, 1, 0)
                .setColor(DyeColor.ORANGE)
                .setInputFilter((itemStack, integer) -> FurnaceTileEntity.isFuel(itemStack))
                .setComponentHarness(this)
        );
    }

    @Override
    public int consumeFuel() {
        int time = ForgeHooks.getBurnTime(fuel.getStackInSlot(0));
        fuel.getStackInSlot(0).shrink(1);
        return time;
    }

    @Override
    public boolean canStart() {
        return !fuel.getStackInSlot(0).isEmpty() && FurnaceTileEntity.getBurnTimes().get(fuel.getStackInSlot(0).getItem()) != null;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 30;
    }

    @Override
    public ProgressBarComponent<PitifulGeneratorTile> getProgressBar() {
        return new ProgressBarComponent<PitifulGeneratorTile>(30, 20, 0, 100)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
    }

    @Override
    public int getEnergyCapacity() {
        return 100000;
    }

    @Override
    public int getExtractingEnergy() {
        return 1000;
    }

    @Override
    public boolean isSmart() {
        return false;
    }

    @Nonnull
    @Override
    public PitifulGeneratorTile getSelf() {
        return this;
    }
}
