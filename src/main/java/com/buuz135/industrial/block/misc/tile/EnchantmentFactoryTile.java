package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentFactoryConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EnchantmentFactoryTile extends IndustrialProcessingTile<EnchantmentFactoryTile> {

    private static final int XP_30 = 1410;

    @Save
    private SidedFluidTankComponent<EnchantmentFactoryTile> tank;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> inputFirst;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> output;

    public EnchantmentFactoryTile() {
        super(ModuleMisc.ENCHANTMENT_FACTORY, 100, 40);
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentFactoryTile>) new SidedFluidTankComponent<EnchantmentFactoryTile>("essence", EnchantmentFactoryConfig.tankSize, 34, 20, 0).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.ESSENCE.getSourceFluid()))
        );
        this.addInventory(inputFirst = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("input", 70, 40, 1, 1).
                setColor(DyeColor.BLUE).
                setInputFilter((stack, integer) -> (!isEnchanted(stack) && stack.isEnchantable()) || stack.getItem().equals(Items.BOOK)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("output", 135, 40, 1, 2).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @Override
    public boolean canIncrease() {
        return this.tank.getFluidAmount() >= XP_30 * 20 && !this.inputFirst.getStackInSlot(0).isEmpty() && this.output.getStackInSlot(0).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack output = EnchantmentHelper.addRandomEnchantment(this.world.rand, this.inputFirst.getStackInSlot(0).copy(), 50, true);
            this.inputFirst.setStackInSlot(0, ItemStack.EMPTY);
            this.output.setStackInSlot(0, output);
            this.tank.drainForced(XP_30 * 20, IFluidHandler.FluidAction.EXECUTE);
        };
    }

    @Override
    protected int getTickPower() {
        return EnchantmentFactoryConfig.powerPerTick;
    }

    @Override
    public EnchantmentFactoryTile getSelf() {
        return this;
    }

    private boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem;
    }

    @Override
    protected EnergyStorageComponent<EnchantmentFactoryTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(EnchantmentFactoryConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return EnchantmentFactoryConfig.maxProgress;
    }
}
