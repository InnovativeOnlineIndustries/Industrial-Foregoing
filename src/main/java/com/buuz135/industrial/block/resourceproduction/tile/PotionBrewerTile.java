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

package com.buuz135.industrial.block.resourceproduction.tile;

import com.buuz135.industrial.block.tile.IndustrialProcessingTile;
import com.buuz135.industrial.config.machine.resourceproduction.ResourcefulFurnaceConfig;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.filter.FilterSlot;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.bundle.LockableInventoryBundle;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.filter.ItemStackFilter;
import com.hrznstudio.titanium.util.InventoryUtil;
import com.hrznstudio.titanium.util.ItemHandlerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotionBrewerTile extends IndustrialProcessingTile<PotionBrewerTile> {

    @Save
    private SidedFluidTankComponent<PotionBrewerTile> water;
    @Save
    private ProgressBarComponent<PotionBrewerTile> blaze;
    @Save
    private SidedInventoryComponent<PotionBrewerTile> blazeInput;
    @Save
    private SidedInventoryComponent<PotionBrewerTile> bottleInput;
    @Save
    private LockableInventoryBundle<PotionBrewerTile> brewingItems;
    @Save
    private SidedInventoryComponent<PotionBrewerTile> output;
    @Save
    private int state;
    @Save
    private ItemStackFilter filter;

    public PotionBrewerTile(BlockPos blockPos, BlockState blockState) {
        super(ModuleResourceProduction.POTION_BREWER, 100, 38, blockPos, blockState);
        this.state = 0;
        addFilter(this.filter = new ItemStackFilter("filter", 1) {
            @Override
            public void onContentChanged() {
                super.onContentChanged();
                markForUpdate();
            }
        });
        var filterSlot = new FilterSlot<>(140, 63, 0, ItemStack.EMPTY);
        filterSlot.setColor(DyeColor.LIME);
        this.filter.setFilter(0, filterSlot);
        addBundle(brewingItems = new LockableInventoryBundle<>(this, new SidedInventoryComponent<PotionBrewerTile>("brewingInput", 55, 19, 6, 3)
                .setColor(DyeColor.BLUE)
                .setInputFilter((stack, integer) -> true)
                .setOutputFilter((stack, integer) -> false),
                148, 40, false));
        addTank(water = (SidedFluidTankComponent<PotionBrewerTile>) new SidedFluidTankComponent<PotionBrewerTile>("water", 1000, 75, 40, 0)
                .setColor(DyeColor.CYAN)
                .setTankAction(FluidTankComponent.Action.FILL)
                .setTankType(FluidTankComponent.Type.SMALL)
                .setValidator(fluidStack -> fluidStack.getFluid().isSame(Fluids.WATER))
        );
        addProgressBar(blaze = new ProgressBarComponent<PotionBrewerTile>(30, 20, 100) {
                    @Override
                    @OnlyIn(Dist.CLIENT)
                    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                        return Collections.singletonList(() -> new ProgressBarScreenAddon<PotionBrewerTile>(30, 20, this) {
                            @Override
                            public List<Component> getTooltipLines() {
                                List<Component> tooltip = new ArrayList<>();
                                tooltip.add(Component.literal(ChatFormatting.GOLD + Component.translatable("text.industrialforegoing.display.blaze_fuel").getString() + ChatFormatting.WHITE + new DecimalFormat().format(blaze.getProgress()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(blaze.getMaxProgress())));
                                return tooltip;
                            }
                        });
                    }
                }
                        .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                        .setColor(DyeColor.ORANGE)
                        .setCanReset(potionBrewerTile -> false)
                        .setCanIncrease(iComponentHarness -> false)
        );
        addInventory(blazeInput = (SidedInventoryComponent<PotionBrewerTile>) new SidedInventoryComponent<PotionBrewerTile>("blazeInput", 45, 61, 1, 2)
                .setColor(DyeColor.ORANGE)
                .setInputFilter((stack, integer) -> stack.getItem().equals(Items.BLAZE_POWDER))
                .setOutputFilter((stack, integer) -> false)
                .setSlotToItemStackRender(0, new ItemStack(Items.BLAZE_POWDER))
        );
        addInventory(bottleInput = (SidedInventoryComponent<PotionBrewerTile>) new SidedInventoryComponent<PotionBrewerTile>("bottleInput", 123, 42, 1, 1)
                .setColor(DyeColor.YELLOW)
                .setInputFilter((stack, integer) -> stack.getItem().equals(Items.GLASS_BOTTLE))
                .setOutputFilter((stack, integer) -> false)
                .setSlotToItemStackRender(0, new ItemStack(Items.GLASS_BOTTLE))
        );
        addInventory(output = (SidedInventoryComponent<PotionBrewerTile>) new SidedInventoryComponent<PotionBrewerTile>("output", 82, 64, 3, 4)
                .setColor(DyeColor.MAGENTA)
                //.setRange(1,3)
                .setSlotLimit(1)
                .setInputFilter((stack, integer) -> false)
                .setOutputFilter((stack, integer) -> filterSlot.getFilter().isEmpty() || this.filter.matches(stack))
        );
    }

    @Override
    public boolean canIncrease() {
        if (blaze.getProgress() + 20 <= blaze.getMaxProgress() && !this.blazeInput.getStackInSlot(0).isEmpty()) {
            this.blazeInput.getStackInSlot(0).shrink(1);
            blaze.setProgress(blaze.getProgress() + 20);
        }
        if (ItemHandlerUtil.isEmpty(output)) {
            this.state = 0;
        }
        if (this.state == 0) {
            return this.water.getFluidAmount() == 1000 && !this.bottleInput.getStackInSlot(0).isEmpty();
        }
        if (this.state >= 7) this.state = 1;
        return canBrew(this.state - 1) && this.blaze.getProgress() > 0;
    }

    @Override
    public Runnable onFinish() {
        return () -> {
            if (this.state == 0) {
                int bottleAmount = Math.min(3, this.bottleInput.getStackInSlot(0).getCount());
                for (int i = 0; i < bottleAmount; i++) {
                    var stack = new ItemStack(Items.POTION);
                    stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER));
                    ItemHandlerHelper.insertItem(this.output, stack, false);
                    this.bottleInput.getStackInSlot(0).shrink(1);
                }
                ++this.state;
            } else {
                brewPotions(this.state - 1);
                this.blaze.setProgress(this.blaze.getProgress() - 1);
                ++this.state;
                if (this.state >= 7) this.state = 1;
            }
        };
    }

    @Override
    public ProgressBarComponent.BarDirection getBarDirection() {
        return ProgressBarComponent.BarDirection.ARROW_DOWN;
    }

    @Override
    protected int getTickPower() {
        return 80;
    }

    @Override
    public PotionBrewerTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<PotionBrewerTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(ResourcefulFurnaceConfig.maxStoredPower, 10, 20);
    }

    private boolean canBrew(int slot) {
        var potionBrewing = this.level.potionBrewing();
        ItemStack ingredient = this.brewingItems.getInventory().getStackInSlot(slot);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!potionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = this.output.getStackInSlot(i);
                if (!itemstack1.isEmpty() && potionBrewing.hasMix(itemstack1, ingredient)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void brewPotions(int slot) {
        NonNullList<ItemStack> input = NonNullList.create();
        ItemStack ingredient = this.brewingItems.getInventory().getStackInSlot(slot);
        input.addAll(InventoryUtil.getStacks(this.output));

        if (EventHooks.onPotionAttemptBrew(input)) return;

        input.replaceAll(potionItem -> this.level.potionBrewing().mix(ingredient, potionItem));

        ingredient.shrink(1);

        EventHooks.onPotionBrewed(input);

        for (int i = 0; i < input.size(); i++) this.output.setStackInSlot(i, input.get(i));
    }

    @Override
    public void loadSettings(Player player, CompoundTag tag) {
        if (tag.contains("BR_locked")) {
            brewingItems.setLocked(tag.getBoolean("BR_locked"));
        }
        if (tag.contains("BR_filter")) {
            for (String psFilter : tag.getCompound("BR_filter").getAllKeys()) {
                brewingItems.getFilter()[Integer.parseInt(psFilter)] = ItemStack.parseOptional(this.level.registryAccess(), tag.getCompound("BR_filter").getCompound(psFilter));
            }
        }
        this.filter.deserializeNBT(this.level.registryAccess(), tag.getCompound("PB_OutputFilter"));
        super.loadSettings(player, tag);
    }

    @Override
    public void saveSettings(Player player, CompoundTag tag) {
        tag.putBoolean("BR_locked", brewingItems.isLocked());
        CompoundTag filterTag = new CompoundTag();
        for (int i = 0; i < brewingItems.getFilter().length; i++) {
            filterTag.put(i + "", brewingItems.getFilter()[i].saveOptional(this.level.registryAccess()));
        }
        tag.put("BR_filter", filterTag);
        tag.put("PB_OutputFilter", this.filter.serializeNBT(this.level.registryAccess()));
        super.saveSettings(player, tag);
    }

}
