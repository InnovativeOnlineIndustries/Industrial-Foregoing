package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentApplicatorConfig;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleMisc;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class EnchantmentApplicatorTile extends IndustrialProcessingTile<EnchantmentApplicatorTile> {

    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> inputFirst;
    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> inputSecond;
    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> output;
    @Save
    private SidedFluidTankComponent<EnchantmentApplicatorTile> tank;

    public EnchantmentApplicatorTile() {
        super(ModuleMisc.ENCHANTMENT_APPLICATOR, 112, 40);
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentApplicatorTile>) new SidedFluidTankComponent<EnchantmentApplicatorTile>("essence", EnchantmentApplicatorConfig.tankSize, 34, 20, 0).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.FILL).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModuleCore.ESSENCE.getSourceFluid()))
        );
        this.addInventory(inputFirst = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("inputFirst", 60, 40, 1, 1).
                setColor(DyeColor.BLUE).
                setInputFilter((stack, integer) -> !stack.getItem().equals(Items.ENCHANTED_BOOK)).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(inputSecond = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("inputSecond", 85, 40, 1, 2).
                setColor(DyeColor.MAGENTA).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("output", 145, 40, 1, 3).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @Override
    public boolean canIncrease() {
        Pair<ItemStack, Integer> output = updateRepairOutput();
        return !output.getLeft().isEmpty() && this.tank.getFluidAmount() >= getEssenceConsumed(output.getRight()) && this.output.getStackInSlot(0).isEmpty();
    }

    private int getEssenceConsumed(int experienceLevel) {
        int xp = 0;
        if (experienceLevel >= 0 && experienceLevel <= 15)
            xp = (int) (Math.pow(experienceLevel, 2) + 6 * experienceLevel);
        if (experienceLevel >= 16 && experienceLevel <= 30)
            xp = (int) (2.5 * Math.pow(experienceLevel, 2) - 40 * experienceLevel + 360);
        if (experienceLevel >= 32) xp = (int) (5.5 * Math.pow(experienceLevel, 2) - 162.5 * experienceLevel + 2220);
        return xp * 20;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            Pair<ItemStack, Integer> output = updateRepairOutput();
            this.inputFirst.setStackInSlot(0, ItemStack.EMPTY);
            this.inputSecond.setStackInSlot(0, ItemStack.EMPTY);
            this.output.setStackInSlot(0, output.getLeft());
            this.tank.drainForced(getEssenceConsumed(output.getRight()), IFluidHandler.FluidAction.EXECUTE);
        };
    }

    @Override
    protected int getTickPower() {
        return EnchantmentApplicatorConfig.powerPerTick;
    }

    @Override
    public EnchantmentApplicatorTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<EnchantmentApplicatorTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(EnchantmentApplicatorConfig.maxStoredPower, 10, 20);
    }

    @Override
    public int getMaxProgress() {
        return EnchantmentApplicatorConfig.maxProgress;
    }

    public Pair<ItemStack, Integer> updateRepairOutput() {
        ItemStack inputFirst = this.inputFirst.getStackInSlot(0);
        int maximumCost = 1;
        int i = 0;
        int j = 0;
        int k = 0;
        if (inputFirst.isEmpty()) {
            return Pair.of(ItemStack.EMPTY, 0);
        } else {
            ItemStack inputFirstCopy = inputFirst.copy();
            ItemStack inputSecond = this.inputSecond.getStackInSlot(0);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(inputFirstCopy);
            j = j + inputFirst.getRepairCost() + (inputSecond.isEmpty() ? 0 : inputSecond.getRepairCost());
            ;
            boolean addEnchantment = false;
            if (!inputSecond.isEmpty()) {
                addEnchantment = inputSecond.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(inputSecond).isEmpty();
                if (inputFirstCopy.isDamageable() && inputFirstCopy.getItem().getIsRepairable(inputFirst, inputSecond)) { //Combine
                    int l2 = Math.min(inputFirstCopy.getDamage(), inputFirstCopy.getMaxDamage() / 4);
                    if (l2 <= 0) { //No Output
                        return Pair.of(ItemStack.EMPTY, 0);
                    }
                    for (int i3 = 0; l2 > 0 && i3 < inputSecond.getCount(); ++i3) {
                        int j3 = inputFirstCopy.getDamage() - l2;
                        inputFirstCopy.setDamage(j3);
                        ++i;
                        l2 = Math.min(inputFirstCopy.getDamage(), inputFirstCopy.getMaxDamage() / 4);
                    }
                } else {
                    if (!addEnchantment && (inputFirstCopy.getItem() != inputSecond.getItem() || !inputFirstCopy.isDamageable())) {
                        return Pair.of(ItemStack.EMPTY, 0); //No output
                    }
                    if (inputFirstCopy.isDamageable() && !addEnchantment) {
                        int l = inputFirst.getMaxDamage() - inputFirst.getDamage();
                        int i1 = inputSecond.getMaxDamage() - inputSecond.getDamage();
                        int j1 = i1 + inputFirstCopy.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = inputFirstCopy.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }
                        if (l1 < inputFirstCopy.getDamage()) {
                            inputFirstCopy.setDamage(l1);
                            i += 2;
                        }
                    }
                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(inputSecond);
                    boolean flag2 = false;
                    boolean flag3 = false;
                    for (Enchantment enchantment1 : map1.keySet()) {
                        if (enchantment1 != null) {
                            int enchantmentValue = map.getOrDefault(enchantment1, 0);
                            int j2 = map1.get(enchantment1);
                            j2 = enchantmentValue == j2 ? j2 + 1 : Math.max(j2, enchantmentValue);
                            boolean flag1 = enchantment1.canApply(inputFirst);
                            for (Enchantment enchantment : map.keySet()) {
                                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                    flag1 = false;
                                    ++i;
                                }
                            }
                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
//                                if (j2 > enchantment1.getMaxLevel()) {
//                                    j2 = enchantment1.getMaxLevel();
//                                }
                                map.put(enchantment1, j2);
                                int enchantmentRarity = 0;
                                switch (enchantment1.getRarity()) {
                                    case COMMON:
                                        enchantmentRarity = 1;
                                        break;
                                    case UNCOMMON:
                                        enchantmentRarity = 2;
                                        break;
                                    case RARE:
                                        enchantmentRarity = 4;
                                        break;
                                    case VERY_RARE:
                                        enchantmentRarity = 8;
                                }
                                if (addEnchantment) {
                                    enchantmentRarity = Math.max(1, enchantmentRarity / 2);
                                }
                                i += enchantmentRarity * j2;
                                if (inputFirst.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }
                    }
                    if (flag3 && !flag2) { //No output
                        return Pair.of(ItemStack.EMPTY, 0);
                    }
                }
            }
            if (addEnchantment && !inputFirstCopy.isBookEnchantable(inputSecond)) inputFirstCopy = ItemStack.EMPTY;
            maximumCost = j + i;
            if (i <= 0) {
                inputFirstCopy = ItemStack.EMPTY;
            }
            if (!inputFirstCopy.isEmpty()) {
                int repairCost = inputFirstCopy.getRepairCost();
                if (!inputSecond.isEmpty() && repairCost < inputSecond.getRepairCost()) {
                    repairCost = inputSecond.getRepairCost();
                }
                if (k != i || k == 0) {
                    repairCost = RepairContainer.getNewRepairCost(repairCost);
                }
                inputFirstCopy.setRepairCost(repairCost);
                EnchantmentHelper.setEnchantments(map, inputFirstCopy);
            }
            //return inputFirstCopy
            return Pair.of(inputFirstCopy, maximumCost);
        }
    }
}
