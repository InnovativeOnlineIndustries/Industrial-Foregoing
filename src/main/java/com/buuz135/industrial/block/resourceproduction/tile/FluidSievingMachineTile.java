package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.FluidSievingMachineConfig;
import com.buuz135.industrial.fluid.OreTitaniumFluidAttributes;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.util.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class FluidSievingMachineTile extends IndustrialProcessingTile<FluidSievingMachineTile> {

    @Save
    private SidedFluidTankComponent<FluidSievingMachineTile> input;
    @Save
    private SidedInventoryComponent<FluidSievingMachineTile> sand;
    @Save
    private SidedInventoryComponent<FluidSievingMachineTile> output;

    public FluidSievingMachineTile() {
        super(ModuleResourceProduction.FLUID_SIEVING_MACHINE,106, 40);
        addTank(this.input = (SidedFluidTankComponent<FluidSievingMachineTile>)  new SidedFluidTankComponent<FluidSievingMachineTile>("input", FluidSievingMachineConfig.maxTankSize, 35, 20, 0){
                    @Override
                    protected void onContentsChanged() {
                        syncObject(FluidSievingMachineTile.this.input);
                    }

                    @Override
                    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
                        return Collections.emptyList();
                    }
                }
                        .setColor(DyeColor.BROWN)
                        .setTankAction(FluidTankComponent.Action.FILL)
                .setValidator(stack -> stack.getFluid().isEquivalentTo(ModuleCore.FERMENTED_ORE_MEAT.getSourceFluid()))
        );
        addInventory(this.sand = (SidedInventoryComponent<FluidSievingMachineTile>) new SidedInventoryComponent<FluidSievingMachineTile>("sand", 60, 31, 4, 1)
                        .setColor(DyeColor.YELLOW)
                .setInputFilter((stack, integer) -> stack.getItem().isIn(ItemTags.SAND))
                .setSlotToItemStackRender(0, new ItemStack(Blocks.SAND))
                .setSlotToItemStackRender(1, new ItemStack(Blocks.SAND))
                .setSlotToItemStackRender(2, new ItemStack(Blocks.SAND))
                .setSlotToItemStackRender(3, new ItemStack(Blocks.SAND))
                .setOutputFilter((stack, integer) -> false)
                .setRange(2,2)
        );
        addInventory(this.output = new SidedInventoryComponent<FluidSievingMachineTile>("output", 140, 40, 1, 2)
                .setColor(DyeColor.ORANGE)
        );
    }

    @Override
    public boolean canIncrease() {
        return this.input.getFluidAmount() >= 100 && !InventoryUtil.getStacks(this.sand).isEmpty() && ItemHandlerHelper.insertItem(this.output, OreTitaniumFluidAttributes.getOutputDust(this.input.getFluid()), true).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            for (int i = 0; i < this.sand.getSlots(); i++) {
                if (!this.sand.getStackInSlot(i).isEmpty()){
                    this.sand.getStackInSlot(i).shrink(1);
                    break;
                }
            }
            ItemStack output = OreTitaniumFluidAttributes.getOutputDust(this.input.getFluid());
            ItemHandlerHelper.insertItem(this.output, output, false);
            this.input.drainForced(100, IFluidHandler.FluidAction.EXECUTE);
        };
    }

    @Override
    public int getMaxProgress() {
        return FluidSievingMachineConfig.maxProgress;
    }

    @Override
    protected int getTickPower() {
        return FluidSievingMachineConfig.powerPerTick;
    }

    @Nonnull
    @Override
    public FluidSievingMachineTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<FluidSievingMachineTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(FluidSievingMachineConfig.maxStoredPower, 10, 20);
    }

}
