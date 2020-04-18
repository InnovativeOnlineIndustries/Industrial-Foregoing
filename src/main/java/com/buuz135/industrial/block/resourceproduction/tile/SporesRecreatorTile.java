package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.SporeRecreatorConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.energy.NBTEnergyHandler;
import com.hrznstudio.titanium.util.ItemHandlerUtil;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class SporesRecreatorTile extends IndustrialProcessingTile<SporesRecreatorTile> {

    private int getPowerPerTick;

    @Save
    private SidedFluidTankComponent<SporesRecreatorTile> tank;
    @Save
    private SidedInventoryComponent<SporesRecreatorTile> input;
    @Save
    private SidedInventoryComponent<SporesRecreatorTile> output;

    public SporesRecreatorTile() {
        super(ModuleResourceProduction.SPORES_RECREATOR, 79, 40);
        addTank(tank = (SidedFluidTankComponent<SporesRecreatorTile>) new SidedFluidTankComponent<SporesRecreatorTile>("water", SporeRecreatorConfig.maxWaterTankSize, 31, 20, 0).
                setColor(DyeColor.CYAN).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(Fluids.WATER))
        );
        addInventory(input = (SidedInventoryComponent<SporesRecreatorTile>) new SidedInventoryComponent<SporesRecreatorTile>("input", 53, 22, 3, 1)
                .setColor(DyeColor.BLUE)
                .setRange(1, 3)
                .setComponentHarness(this)
                .setInputFilter((stack, integer) -> stack.getItem().isIn(Tags.Items.MUSHROOMS))
                .setOutputFilter((stack, integer) -> false)
        );
        addInventory(output = (SidedInventoryComponent<SporesRecreatorTile>) new SidedInventoryComponent<SporesRecreatorTile>("output", 110, 22, 9, 2)
                .setColor(DyeColor.ORANGE)
                .setRange(3, 3)
                .setComponentHarness(this)
                .setInputFilter((stack, integer) -> false)
        );
        this.getPowerPerTick = SporeRecreatorConfig.powerPerTick;
    }

    @Override
    public boolean canIncrease() {
        return !ItemHandlerUtil.getFirstItem(input).isEmpty() && tank.getFluidAmount() >= 500 && ItemHandlerHelper.insertItem(output, new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack outputStack = new ItemStack(ItemHandlerUtil.getFirstItem(input).getItem(), 2);
            tank.drainForced(500, IFluidHandler.FluidAction.EXECUTE);
            ItemHandlerUtil.getFirstItem(input).shrink(1);
            ItemHandlerHelper.insertItem(output, outputStack, false);
        };
    }

    @Override
    protected IFactory<NBTEnergyHandler> getEnergyHandlerFactory() {
        return () -> new NBTEnergyHandler(this, SporeRecreatorConfig.maxStoredPower);
    }

    @Override
    protected int getTickPower() {
        return getPowerPerTick;
    }

    @Nonnull
    @Override
    public SporesRecreatorTile getSelf() {
        return this;
    }
}
