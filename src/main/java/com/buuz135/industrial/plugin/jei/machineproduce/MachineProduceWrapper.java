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
package com.buuz135.industrial.plugin.jei.machineproduce;

import net.minecraft.tags.TagKey;
import net.minecraftforge.fluids.FluidStack;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class MachineProduceWrapper {

    private Block block;
    private Ingredient outputItem;
    private FluidStack outputFluid;

    public MachineProduceWrapper(Block block, ItemStack... output) {
        this.block = block;
        this.outputItem = Ingredient.of(output);
        this.outputFluid = FluidStack.EMPTY;
    }

    public MachineProduceWrapper(Block block, TagKey<Item> output) {
        this.block = block;
        this.outputItem = Ingredient.of(output);
        this.outputFluid = FluidStack.EMPTY;
    }

    public MachineProduceWrapper(Block block, FluidStack output) {
        this.block = block;
        this.outputItem = null;
        this.outputFluid = output;
    }

    public Block getBlock() {
        return block;
    }

    public Ingredient getOutputItem() {
        return outputItem;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }
}
