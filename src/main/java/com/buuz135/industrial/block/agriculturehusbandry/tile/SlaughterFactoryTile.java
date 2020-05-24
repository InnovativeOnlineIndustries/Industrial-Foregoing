package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.SlaughterFactoryConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class SlaughterFactoryTile extends IndustrialAreaWorkingTile<SlaughterFactoryTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    private SidedFluidTankComponent<SlaughterFactoryTile> meat;
    @Save
    private SidedFluidTankComponent<SlaughterFactoryTile> pinkSlime;

    public SlaughterFactoryTile() {
        super(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY, RangeManager.RangeType.BEHIND, true);
        addTank(meat = (SidedFluidTankComponent<SlaughterFactoryTile>) new SidedFluidTankComponent<SlaughterFactoryTile>("meat", SlaughterFactoryConfig.maxMeatTankSize, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
        addTank(pinkSlime = (SidedFluidTankComponent<SlaughterFactoryTile>) new SidedFluidTankComponent<SlaughterFactoryTile>("pink_slime", SlaughterFactoryConfig.maxPinkSlimeTankSize, 63, 20, 1).
                setColor(DyeColor.PINK).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.PINK_SLIME.getSourceFluid())));
        this.maxProgress = SlaughterFactoryConfig.maxProgress;
        this.powerPerOperation = SlaughterFactoryConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(powerPerOperation)) {
            List<MobEntity> mobs = this.world.getEntitiesWithinAABB(MobEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                MobEntity entity = mobs.get(0);
                float currentHealth = entity.getHealth();
                entity.remove(true);
                if (!entity.isAlive()) {
                    meat.fillForced(new FluidStack(ModuleCore.MEAT.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth) : (int) currentHealth * 20), IFluidHandler.FluidAction.EXECUTE);
                    pinkSlime.fillForced(new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth * 20) : (int) currentHealth), IFluidHandler.FluidAction.EXECUTE);
                    return new WorkAction(0.2f, powerPerOperation);
                }
            }
        }
        return new WorkAction(1, 0);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, SlaughterFactoryConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public SlaughterFactoryTile getSelf() {
        return this;
    }
}
