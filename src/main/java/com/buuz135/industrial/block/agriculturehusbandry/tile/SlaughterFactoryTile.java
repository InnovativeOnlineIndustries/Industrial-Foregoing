package com.buuz135.industrial.block.agriculturehusbandry.tile;

import com.buuz135.industrial.block.tile.IndustrialAreaWorkingTile;
import com.buuz135.industrial.block.tile.RangeManager;
import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.buuz135.industrial.module.ModuleCore;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.tile.fluid.PosFluidTank;
import com.hrznstudio.titanium.block.tile.fluid.SidedFluidTank;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class SlaughterFactoryTile extends IndustrialAreaWorkingTile {

    @Save
    private SidedFluidTank meat;
    @Save
    private SidedFluidTank pinkSlime;

    public SlaughterFactoryTile() {
        super(ModuleAgricultureHusbandry.SLAUGHTER_FACTORY, RangeManager.RangeType.BEHIND);
        addTank(meat = (SidedFluidTank) new SidedFluidTank("meat", 8000, 43, 20, 0).
                setColor(DyeColor.BROWN).
                setTankAction(PosFluidTank.Action.DRAIN).
                setTile(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
        addTank(pinkSlime = (SidedFluidTank) new SidedFluidTank("pink_slime", 8000, 63, 20, 1).
                setColor(DyeColor.PINK).
                setTankAction(PosFluidTank.Action.DRAIN).
                setTile(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.PINK_SLIME.getSourceFluid())));
    }

    @Override
    public WorkAction work() {
        if (hasEnergy(400)) {
            List<MobEntity> mobs = this.world.getEntitiesWithinAABB(MobEntity.class, getWorkingArea().getBoundingBox());
            if (mobs.size() > 0) {
                MobEntity entity = mobs.get(0);
                float currentHealth = entity.getHealth();
                entity.remove(true);
                if (!entity.isAlive()) {
                    meat.fillForced(new FluidStack(ModuleCore.MEAT.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth) : (int) currentHealth * 20), IFluidHandler.FluidAction.EXECUTE);
                    pinkSlime.fillForced(new FluidStack(ModuleCore.PINK_SLIME.getSourceFluid(), entity instanceof AnimalEntity ? (int) (currentHealth * 20) : (int) currentHealth), IFluidHandler.FluidAction.EXECUTE);
                    return new WorkAction(0.2f, 400);
                }
            }
        }
        return new WorkAction(1, 0);
    }
}
