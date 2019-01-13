/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
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
package com.buuz135.industrial.tile.generator;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class PitifulFuelGeneratorTile extends AbstractFuelGenerator {

    public PitifulFuelGeneratorTile() {
        super(PitifulFuelGeneratorTile.class.getName().hashCode());
    }

    public static long getEnergy(int burnTime) {
        return burnTime / BlockRegistry.pitifulFuelGeneratorBlock.getTimeModifier();
    }

    public static boolean acceptsInputStack(ItemStack stack) {
        return !stack.isEmpty() && TileEntityFurnace.isItemFuel(stack) && isWoodRelated(stack);
    }

    public static boolean isWoodRelated(ItemStack stack) {
        String[] woods = new String[]{"logWood", "plankWood", "slabWood", "stairWood", "fenceWood", "fenceGateWood", "doorWood", "stickWood"};
        for (String wood : woods) {
            if (ItemStackUtils.isStackOreDict(stack, wood)) return true;
        }
        return false;
    }

    @Override
    public boolean acceptsItemStack(ItemStack stack) {
        return acceptsInputStack(stack);
    }

    @Override
    public long getEnergyProduced(int burnTime) {
        return getEnergy(burnTime);
    }

    @Override
    public float getMultiplier() {
        return BlockRegistry.pitifulFuelGeneratorBlock.getBurnTimeMultiplier();
    }
}
