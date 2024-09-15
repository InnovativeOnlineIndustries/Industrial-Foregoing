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
import com.buuz135.industrial.config.machine.misc.EnchantmentFactoryConfig;
import com.buuz135.industrial.gui.component.TextureScreenAddon;
import com.buuz135.industrial.module.ModuleMisc;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.client.screen.addon.TextScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantmentFactoryTile extends IndustrialProcessingTile<EnchantmentFactoryTile> {

    private static final int XP_30 = 1410;

    @Save
    private SidedFluidTankComponent<EnchantmentFactoryTile> tank;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> inputFirst;
    @Save
    private SidedInventoryComponent<EnchantmentFactoryTile> output;
    @Save
    private int selectedLevel;

    public EnchantmentFactoryTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleMisc.ENCHANTMENT_FACTORY, 100, 54, blockPos, blockState);
        this.selectedLevel = 30;
        this.addTank(tank = (SidedFluidTankComponent<EnchantmentFactoryTile>) new SidedFluidTankComponent<EnchantmentFactoryTile>("essence", EnchantmentFactoryConfig.tankSize, 34, 20, 0).
                setColor(DyeColor.LIME).
                setTankAction(FluidTankComponent.Action.FILL).
                setOnContentChange(() -> syncObject(this.tank)).
                setComponentHarness(this).
                setValidator(fluidStack -> fluidStack.getFluid().is(IndustrialTags.Fluids.EXPERIENCE))
        );
        this.addInventory(inputFirst = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("input", 70, 54, 1, 1).
                setColor(DyeColor.BLUE).
                setInputFilter((stack, integer) -> (!isEnchanted(stack) && stack.isEnchantable()) || stack.getItem().equals(Items.BOOK)).
                setSlotLimit(1).
                setOutputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
        this.addInventory(output = (SidedInventoryComponent<EnchantmentFactoryTile>) new SidedInventoryComponent<EnchantmentFactoryTile>("output", 135, 54, 1, 2).
                setColor(DyeColor.ORANGE).
                setInputFilter((stack, integer) -> false).
                setComponentHarness(this)
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> new TextScreenAddon(Component.translatable("text.industrialforegoing.display.enchantment_level").getString(), 60, 24, false));
        this.addGuiAddonFactory(() -> new TextScreenAddon(Component.translatable("text.industrialforegoing.display.cost").getString(), 60, 38, false) {
            @Override
            public String getText() {
                return super.getText() + ChatFormatting.DARK_GREEN + new DecimalFormat().format(EnchantmentApplicatorTile.getEssenceConsumed(selectedLevel)) + ChatFormatting.DARK_GRAY + "mb";
            }
        });
        this.addGuiAddonFactory(() -> {
            var edit = new EditBox(Minecraft.getInstance().font, 80, 26, 40, 12, Component.literal(this.selectedLevel + "")) {
                @Override
                public String getValue() {
                    return selectedLevel + "";
                }
            };
            edit.setValue(selectedLevel + "");
            edit.setFilter(string -> {
                if (string.isEmpty()) return true;
                try {
                    Integer.decode(string);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            edit.setResponder(string -> {
                if (!string.isEmpty()) {
                    CompoundTag compoundTag = new CompoundTag();
                    compoundTag.putInt("EnchantmentLevel", Integer.decode(string));
                    Titanium.NETWORK.sendToServer(new ButtonClickNetworkMessage(new TileEntityLocatorInstance(this.getBlockPos()), 5487, compoundTag));
                }
            });
            return new WidgetScreenAddon(120, 22, edit);
        });
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
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 5487) {
            this.selectedLevel = compound.getInt("EnchantmentLevel");
            syncObject(this.selectedLevel);
        }
    }

    @Override
    public boolean canIncrease() {
        long amount = this.tank.getFluidAmount();
        var capability = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition.above(), Direction.DOWN);
        if (capability != null) {
            amount += EnchantmentApplicatorTile.getMatchingAmount(capability);
        }
        return amount >= EnchantmentApplicatorTile.getEssenceConsumed(this.selectedLevel) && !this.inputFirst.getStackInSlot(0).isEmpty() && this.output.getStackInSlot(0).isEmpty();
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            ItemStack output = EnchantmentHelper.enchantItem(this.level.random, this.inputFirst.getStackInSlot(0).copy(), this.selectedLevel, this.level.registryAccess(), Optional.empty());
            this.inputFirst.setStackInSlot(0, ItemStack.EMPTY);
            this.output.setStackInSlot(0, output);
            var amount = EnchantmentApplicatorTile.getEssenceConsumed(this.selectedLevel);
            var capability = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition.above(), Direction.DOWN);
            if (capability != null) {
                amount = EnchantmentApplicatorTile.drainAmount(amount, capability);
            }
            if (amount > 0) this.tank.drainForced(amount, IFluidHandler.FluidAction.EXECUTE);
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

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        super.loadSettings(player, tag);
        this.selectedLevel = tag.getInt("SelectedLevel");
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        super.saveSettings(player, tag);
        tag.putInt("SelectedLevel", this.selectedLevel);
    }
}
