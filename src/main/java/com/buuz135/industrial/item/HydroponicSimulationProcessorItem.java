/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2026, Buuz135
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
package com.buuz135.industrial.item;

import com.buuz135.industrial.utils.IndustrialTags;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HydroponicSimulationProcessorItem extends IFCustomItem {

    public static final String ITEM_NAME = "hydroponic_simulation_processor";

    public HydroponicSimulationProcessorItem(TitaniumTab tab)
    {
        super(ITEM_NAME, tab, new Properties().stacksTo(1));
    }

    public static double calculateEfficiency(double executions)
    {
        return Math.log(executions) / Math.log(200);
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PCP").pattern("DRD").pattern("PGP")
                .define('P', IndustrialTags.Items.PLASTIC)
                .define('C', Items.COMPARATOR)
                .define('D',Items.OBSERVER)
                .define('R',Items.REPEATER)
                .define('G', IndustrialTags.Items.GEAR_DIAMOND)
                .save(consumer);
    }

    @Override
    public void addTooltipDetails(@Nullable BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced)
    {
        super.addTooltipDetails(key, stack, tooltip, advanced);

        // get nbt data
        CompoundTag tag = stack.getOrCreateTag();

        // read simulation data
        var simulation = new Simulation(tag.getCompound(ITEM_NAME));

        var effi = Math.floor(calculateEfficiency(simulation.executions) * 100) / 100;
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.simulating").withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(simulation.crop.isEmpty() ?
                        "tooltip.industrialforegoing.hydroponic.nothing":
                        simulation.crop.getDescriptionId()).withStyle(ChatFormatting.GOLD)));
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.executions").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(new DecimalFormat().format(simulation.executions)).withStyle(ChatFormatting.GOLD)));
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.efficiency").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(effi + "").withStyle(ChatFormatting.GOLD)));
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.next_efficiency").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(getProgressBar(simulation.executions) + "").withStyle(ChatFormatting.GOLD)));
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.potential_drops").withStyle(ChatFormatting.GRAY));
        for (CountedStack stat : simulation.stats) {
            tooltip.add(Component.literal(ChatFormatting.GRAY + " - " + ChatFormatting.WHITE + new DecimalFormat("0.00").format(((stat.amount / (double) simulation.executions) * effi)) + ChatFormatting.GRAY + "x " + ChatFormatting.GOLD + Component.translatable(stat.stack.getDescriptionId()).getString()));
        }
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.function_1").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.industrialforegoing.hydroponic.function_2").withStyle(ChatFormatting.DARK_GRAY));

    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    private String getProgressBar(long executions) {
        var currentEff = Math.floor(calculateEfficiency(executions) * 100) / 100;
        var nextEff = calculateNextEfficiency(currentEff);
        var lastEff = Math.floor(currentEff * 100) / 100;
        lastEff = calculateNextEfficiency(lastEff - 0.01);
        var linesAmount = 30;
        var progress = (int) Math.floor((executions - lastEff) / (nextEff - lastEff) * linesAmount);
        return ChatFormatting.GOLD + "[" + "|".repeat(progress) + ChatFormatting.DARK_GRAY + "|".repeat(linesAmount - progress) + ChatFormatting.GOLD + "]";
    }

    private double calculateNextEfficiency(double executions) {
        return Math.ceil(Math.pow(200, executions + 0.01));
    }

    public static class Simulation
    {
        private long executions;
        private ItemStack crop;
        private List<CountedStack> stats;

        public Simulation()
        {
            this.executions = 0;
            this.crop = ItemStack.EMPTY;
            this.stats = new ArrayList<>();
        }

        public Simulation(CompoundTag nbt)
        {
            this.crop = ItemStack.of(nbt.getCompound("Crop"));
            this.executions = nbt.getLong("Executions");
            this.stats = new ArrayList<>();
            var statsCompound = nbt.getCompound("Stats");
            for(String allKey : statsCompound.getAllKeys())
            {
                var stat = statsCompound.getCompound(allKey);
                this.stats.add(
                        new CountedStack(
                                ItemStack.of(stat.getCompound("Stack")),
                                        stat.getInt("Count")
                                )
                        );
            }
        }

        public CompoundTag toNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.put("Crop", crop.save(new CompoundTag()));
            nbt.putLong("Executions", executions);
            var allStats = new CompoundTag();
            int key = 0;
            for (CountedStack stat : this.stats) {
                var statCompound = new CompoundTag();
                statCompound.put("Stack", stat.stack.save(new CompoundTag()));
                statCompound.putLong("Count", stat.amount);
                allStats.put(key + "", statCompound);
                ++key;
            }
            nbt.put("Stats", allStats);
            return nbt;
        }

        public void acceptExecution(ItemStack crop, List<ItemStack> stacks) {
            if (this.crop.isEmpty()) this.crop = crop.copyWithCount(1);
            if (ItemStack.isSameItem(this.crop, crop)) {
                ++this.executions;
                for (ItemStack itemStack : stacks) {
                    if (itemStack.isEmpty()) continue;
                    var found = false;
                    for (CountedStack stat : this.stats) {
                        if (ItemStack.isSameItem(itemStack, stat.stack)) {
                            found = true;
                            stat.amount += itemStack.getCount();
                            break;
                        }
                    }
                    if (!found) {
                        this.stats.add(new CountedStack(itemStack.copy(), itemStack.getCount()));
                    }
                }
            }
        }

        public long getExecutions() {
            return executions;
        }

        public ItemStack getCrop() {
            return crop;
        }

        public List<CountedStack> getStats() {
            return stats;
        }
    }

    public static final class CountedStack {
        private ItemStack stack;
        private long amount;

        private CountedStack(ItemStack stack, int amount) {
            this.stack = stack;
            this.amount = amount;
        }

        public ItemStack stack() {
            return stack;
        }

        public long amount() {
            return amount;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (CountedStack) obj;
            return Objects.equals(this.stack, that.stack) &&
                    this.amount == that.amount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack, amount);
        }

        @Override
        public String toString() {
            return "CountedStack[" +
                    "stack=" + stack + ", " +
                    "amount=" + amount + ']';
        }


    }
}
