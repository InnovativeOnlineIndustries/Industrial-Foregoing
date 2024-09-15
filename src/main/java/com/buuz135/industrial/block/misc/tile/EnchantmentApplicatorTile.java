/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.block.misc.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.misc.EnchantmentApplicatorConfig;
import com.buuz135.industrial.gui.component.TextureScreenAddon;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.util.TagUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentApplicatorTile extends IndustrialProcessingTile<EnchantmentApplicatorTile> {

    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> inputFirst;
    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> inputSecond;
    @Save
    private SidedInventoryComponent<EnchantmentApplicatorTile> output;
    @Save
    private SidedFluidTankComponent<EnchantmentApplicatorTile> tank;

    public EnchantmentApplicatorTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleMisc.ENCHANTMENT_APPLICATOR, 112, 40, blockPos, blockState);
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentApplicatorTile>) new SidedFluidTankComponent<EnchantmentApplicatorTile>("essence", EnchantmentApplicatorConfig.tankSize, 34, 20, 0).
                setColor(DyeColor.LIME).
                setComponentHarness(this).
                setOnContentChange(() -> syncObject(this.tank)).
                setValidator(fluidStack -> TagUtil.hasTag(BuiltInRegistries.FLUID, fluidStack.getFluid(), IndustrialTags.Fluids.EXPERIENCE))
        );
        this.addInventory(inputFirst = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("inputFirst", 60, 40, 1, 1).
                setColor(DyeColor.BLUE).
                setInputFilter((stack, integer) -> !stack.getItem().equals(Items.ENCHANTED_BOOK)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(inputSecond = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("inputSecond", 85, 40, 1, 2).
                setColor(DyeColor.MAGENTA).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<EnchantmentApplicatorTile>) new SidedInventoryComponent<EnchantmentApplicatorTile>("output", 145, 40, 1, 3).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    public static int getMatchingAmount(IFluidHandler capability) {
        int amount = 0;
        for (int i = 0; i < capability.getTanks(); i++) {
            if (capability.getFluidInTank(i).is(IndustrialTags.Fluids.EXPERIENCE)) {
                amount += capability.getFluidInTank(i).getAmount();
            }
        }
        return amount;
    }

    public static int drainAmount(int amount, IFluidHandler capability) {
        for (int i = 0; i < capability.getTanks(); i++) {
            if (capability.getFluidInTank(i).is(IndustrialTags.Fluids.EXPERIENCE)) {
                amount -= capability.drain(amount, IFluidHandler.FluidAction.EXECUTE).getAmount();
            }
        }
        return amount;
    }

    public static int getEssenceConsumed(int experienceLevel) {
        long xp = 0;
        if (experienceLevel >= 0 && experienceLevel <= 16)
            xp = (long) (Math.pow(experienceLevel, 2) + 6 * experienceLevel);
        if (experienceLevel >= 17 && experienceLevel <= 31)
            xp = (long) (2.5 * Math.pow(experienceLevel, 2) - 40.5 * experienceLevel + 360);
        if (experienceLevel >= 32) xp = (long) (4.5 * Math.pow(experienceLevel, 2) - 162.5 * experienceLevel + 2220);
        xp = xp * 20;
        return xp > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) xp;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        ResourceLocation res = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/machines.png");
        this.addGuiAddonFactory(() -> new TextureScreenAddon(158, 4, 14, 14, res, 96, 233) {

            @Override
            public List<Component> getTooltipLines() {
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("text.industrialforegoing.tooltip.accepts_fluid_on_top"));
                return components;
            }
        });
    }

    @Override
    public boolean canIncrease() {
        Pair<ItemStack, Integer> output = updateRepairOutput();
        long amount = this.tank.getFluidAmount();
        var capability = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition.above(), Direction.DOWN);
        if (capability != null) {
            amount += getMatchingAmount(capability);
        }
        return !output.getLeft().isEmpty() && amount >= getEssenceConsumed(output.getRight()) && this.output.getStackInSlot(0).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            Pair<ItemStack, Integer> output = updateRepairOutput();
            this.inputFirst.setStackInSlot(0, ItemStack.EMPTY);
            this.inputSecond.getStackInSlot(0).shrink(1);
            this.output.setStackInSlot(0, output.getLeft());
            var amount = getEssenceConsumed(output.getRight());
            var capability = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition.above(), Direction.DOWN);
            if (capability != null) {
                amount = drainAmount(amount, capability);
            }
            if (amount > 0) this.tank.drainForced(amount, IFluidHandler.FluidAction.EXECUTE);
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
            ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(inputFirstCopy));
            j = j + inputFirst.get(DataComponents.REPAIR_COST) + (inputSecond.isEmpty() ? 0 : inputSecond.get(DataComponents.REPAIR_COST));

            boolean addEnchantment = false;
            if (!inputSecond.isEmpty()) {
                addEnchantment = inputSecond.has(DataComponents.STORED_ENCHANTMENTS);
                if (inputFirstCopy.isDamageableItem() && inputFirstCopy.getItem().isValidRepairItem(inputFirst, inputSecond)) { //Combine
                    int l2 = Math.min(inputFirstCopy.getDamageValue(), inputFirstCopy.getMaxDamage() / 4);
                    if (l2 <= 0) { //No Output
                        return Pair.of(ItemStack.EMPTY, 0);
                    }
                    for (int i3 = 0; l2 > 0 && i3 < inputSecond.getCount(); ++i3) {
                        int j3 = inputFirstCopy.getDamageValue() - l2;
                        inputFirstCopy.setDamageValue(j3);
                        ++i;
                        l2 = Math.min(inputFirstCopy.getDamageValue(), inputFirstCopy.getMaxDamage() / 4);
                    }
                } else {
                    if (!addEnchantment && (inputFirstCopy.getItem() != inputSecond.getItem() || !inputFirstCopy.isDamageableItem())) {
                        return Pair.of(ItemStack.EMPTY, 0); //No output
                    }
                    if (inputFirstCopy.isDamageableItem() && !addEnchantment) {
                        int l = inputFirst.getMaxDamage() - inputFirst.getDamageValue();
                        int i1 = inputSecond.getMaxDamage() - inputSecond.getDamageValue();
                        int j1 = i1 + inputFirstCopy.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = inputFirstCopy.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }
                        if (l1 < inputFirstCopy.getDamageValue()) {
                            inputFirstCopy.setDamageValue(l1);
                            i += 2;
                        }
                    }
                    ItemEnchantments map1 = EnchantmentHelper.getEnchantmentsForCrafting(inputSecond);
                    boolean flag2 = false;
                    boolean flag3 = false;
                    for (Object2IntMap.Entry<Holder<Enchantment>> enchantment1 : map1.entrySet()) {
                        if (enchantment1 != null) {
                            Holder<Enchantment> holder = enchantment1.getKey();
                            int enchantmentValue = map.getLevel(holder);
                            int j2 = enchantment1.getIntValue();
                            j2 = enchantmentValue == j2 ? j2 + 1 : Math.max(j2, enchantmentValue);
                            boolean flag1 = inputFirst.supportsEnchantment(holder);
                            for (Holder<Enchantment> enchantment : map.keySet()) {
                                if (!enchantment.equals(enchantment1) && !Enchantment.areCompatible(holder, enchantment)) {
                                    flag1 = false;
                                    ++i;
                                }
                            }
                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
                                if (!EnchantmentApplicatorConfig.ignoreEnchantMaxLevels && j2 > holder.value().getMaxLevel()) {
                                    j2 = holder.value().getMaxLevel();
                                }
                                map.set(holder, j2);
                                int enchantmentRarity = holder.value().getAnvilCost();

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
                int repairCost = inputFirstCopy.get(DataComponents.REPAIR_COST);
                if (!inputSecond.isEmpty() && repairCost < inputSecond.get(DataComponents.REPAIR_COST)) {
                    repairCost = inputSecond.get(DataComponents.REPAIR_COST);
                }
                if (k != i || k == 0) {
                    repairCost = AnvilMenu.calculateIncreasedRepairCost(repairCost);
                }
                inputFirstCopy.set(DataComponents.REPAIR_COST, repairCost);
                EnchantmentHelper.setEnchantments(inputFirstCopy, map.toImmutable());
            }
            //return inputFirstCopy
            return Pair.of(inputFirstCopy, maximumCost);
        }
    }
}
