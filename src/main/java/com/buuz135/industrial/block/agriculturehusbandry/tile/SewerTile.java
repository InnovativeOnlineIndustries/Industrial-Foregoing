package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.config.machine.agriculturehusbandry.SewerConfig;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class SewerTile extends IndustrialAreaWorkingTile<SewerTile> {

    private int maxProgress;
    private int powerPerOperation;

    @Save
    public SidedFluidTankComponent<SewerTile> sewage;
    @Save
    public SidedFluidTankComponent<SewerTile> essence;

    public SewerTile() {
        super(ModuleAgricultureHusbandry.SEWER, RangeManager.RangeType.TOP);
        this.addTank(sewage = (SidedFluidTankComponent<SewerTile>) new SidedFluidTankComponent<SewerTile>("sewage", SewerConfig.maxSewageTankSize, 45, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this));
        this.addTank(essence = (SidedFluidTankComponent<SewerTile>) new SidedFluidTankComponent<SewerTile>("essence", SewerConfig.maxEssenceTankSize, 66, 20, 1).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.DRAIN).
                setComponentHarness(this));
        this.maxProgress = SewerConfig.maxProgress;
        this.powerPerOperation = SewerConfig.powerPerOperation;
    }

    @Override
    public WorkAction work() {
        List<Entity> entities = this.world.getEntitiesWithinAABB(AnimalEntity.class, getWorkingArea().getBoundingBox());
        int amount = entities.size();
        if (hasEnergy(powerPerOperation * amount)) {
            sewage.fillForced(new FluidStack(ModuleCore.SEWAGE.getSourceFluid(), 50), IFluidHandler.FluidAction.EXECUTE);
            ++amount;
        }
        List<ExperienceOrbEntity> orb = this.world.getEntitiesWithinAABB(ExperienceOrbEntity.class, getWorkingArea().getBoundingBox());
        for (ExperienceOrbEntity experienceOrbEntity : orb) {
            if (experienceOrbEntity.isAlive() && essence.getFluidAmount() + experienceOrbEntity.xpValue * 20 <= essence.getCapacity()) {
                essence.fillForced(new FluidStack(ModuleCore.ESSENCE.getSourceFluid(), experienceOrbEntity.xpValue * 20), IFluidHandler.FluidAction.EXECUTE);
                experienceOrbEntity.remove();
            }
        }
        return new WorkAction(1, powerPerOperation * amount);
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, SewerConfig.maxStoredPower);
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Nonnull
    @Override
    public SewerTile getSelf() {
        return this;
    }
}
