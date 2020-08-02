package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.SludgeRefinerConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SludgeRefinerTile extends IndustrialProcessingTile<SludgeRefinerTile> {

    private int getPowerPerTick;

    @Save
    private SidedFluidTankComponent<SludgeRefinerTile> sludge;
    @Save
    private SidedInventoryComponent<SludgeRefinerTile> output;

    public SludgeRefinerTile() {
        super(ModuleResourceProduction.SLUDGE_REFINER, 53, 40);
        addTank(sludge = (SidedFluidTankComponent<SludgeRefinerTile>) new SidedFluidTankComponent<SludgeRefinerTile>("sludge", SludgeRefinerConfig.maxSludgeTankSize, 31, 20, 0)
                .setColor(DyeColor.MAGENTA)
                .setComponentHarness(this)
                .setTankAction(FluidTankComponent.Action.FILL)
        );
        addInventory(output = (SidedInventoryComponent<SludgeRefinerTile>) new SidedInventoryComponent<SludgeRefinerTile>("output", 80, 22, 5 * 3, 1)
                .setColor(DyeColor.ORANGE)
                .setRange(5, 3)
                .setInputFilter((stack, integer) -> false)
                .setComponentHarness(this)
        );
        this.getPowerPerTick = SludgeRefinerConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        return sludge.getFluidAmount() >= 500;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            Item item = this.world.getTags().getItems().get(IndustrialTags.Items.SLUDGE_OUTPUT.getName()).getRandomElement(this.world.rand);
            if (item != null && ItemHandlerHelper.insertItem(output, new ItemStack(item), true).isEmpty()) {
                sludge.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
                ItemHandlerHelper.insertItem(output, new ItemStack(item), false);
            }
        };
    }

    @Override
    protected EnergyStorageComponent<SludgeRefinerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(SludgeRefinerConfig.maxStoredPower, 10, 20);
    }

    @Override
    protected int getTickPower() {
        return getPowerPerTick;
    }

    @Nonnull
    @Override
    public SludgeRefinerTile getSelf() {
        return this;
    }
}
