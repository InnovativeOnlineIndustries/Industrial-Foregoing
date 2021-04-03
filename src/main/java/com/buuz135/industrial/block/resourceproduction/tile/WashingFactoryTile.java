package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.WashingFactoryConfig;
import com.buuz135.industrial.fluid.OreTitaniumFluidAttributes;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class WashingFactoryTile extends IndustrialProcessingTile<WashingFactoryTile> {

    @Save
    private SidedInventoryComponent<WashingFactoryTile> input;
    @Save
    private SidedFluidTankComponent<WashingFactoryTile> meatInput;
    @Save
    private SidedFluidTankComponent<WashingFactoryTile> meatOutput;

    public WashingFactoryTile() {
        super(ModuleResourceProduction.WASHING_FACTORY, 100, 40);
        addInventory(this.input = (SidedInventoryComponent<WashingFactoryTile>) new SidedInventoryComponent<WashingFactoryTile>("input", 40, 40, 1, 0)
                .setColor(DyeColor.BLUE)
                .setInputFilter((stack, integer) -> {
                    if (!stack.getItem().isIn(Tags.Items.ORES)) return false;
                    for (ResourceLocation resourceLocation : stack.getItem().getTags()) {
                        if (resourceLocation.toString().startsWith("forge:ores/") && OreTitaniumFluidAttributes.isValid(resourceLocation)){
                            return true;
                        }
                    }
                    return false;
                })
        );
        addTank(this.meatInput = (SidedFluidTankComponent<WashingFactoryTile>) new SidedFluidTankComponent<WashingFactoryTile>("meat", WashingFactoryConfig.maxTankSize, 70, 20, 1)
                .setColor(DyeColor.BROWN)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.MEAT.getSourceFluid()))
        );
        addTank(this.meatOutput = (SidedFluidTankComponent<WashingFactoryTile>)  new SidedFluidTankComponent<WashingFactoryTile>("output", WashingFactoryConfig.maxOutputSize, 135, 20, 2){
                    @Override
                    protected void onContentsChanged() {
                        syncObject(WashingFactoryTile.this.meatOutput);
                    }

                    @Override
                    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
                        return Collections.emptyList();
                    }
                }
                .setColor(DyeColor.ORANGE)
                .setTankAction(FluidTankComponent.Action.DRAIN)
        );
    }

    @Override
    public boolean canIncrease() {
        if (this.input.getStackInSlot(0).isEmpty()) return false;
        ResourceLocation resourceLocation = ItemStackUtils.getOreTag(this.input.getStackInSlot(0));
        if (resourceLocation == null) return false;
        FluidStack output = OreTitaniumFluidAttributes.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation);
        return this.meatInput.getFluidAmount() >= 100 && this.meatOutput.fillForced(output, IFluidHandler.FluidAction.SIMULATE) == 100;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ResourceLocation resourceLocation = ItemStackUtils.getOreTag(this.input.getStackInSlot(0));
            this.input.getStackInSlot(0).shrink(1);
            this.meatInput.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
            FluidStack output = OreTitaniumFluidAttributes.getFluidWithTag(ModuleCore.RAW_ORE_MEAT, 100, resourceLocation);
            this.meatOutput.fillForced(output, IFluidHandler.FluidAction.EXECUTE);
        };
    }

    @Override
    public int getMaxProgress() {
        return WashingFactoryConfig.maxProgress;
    }

    @Override
    protected int getTickPower() {
        return WashingFactoryConfig.powerPerTick;
    }

    @Nonnull
    @Override
    public WashingFactoryTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<WashingFactoryTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(WashingFactoryConfig.maxStoredPower, 10, 20);
    }

}
