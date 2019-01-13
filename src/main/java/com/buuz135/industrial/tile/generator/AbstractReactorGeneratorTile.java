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

import com.buuz135.industrial.tile.CustomGeneratorMachine;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

public abstract class AbstractReactorGeneratorTile extends CustomGeneratorMachine {

    private IFluidTank tank;
    private int generatedPower;

    public AbstractReactorGeneratorTile(int id, int generatedPower) {
        super(id);
        this.generatedPower = generatedPower;
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addFluidTank(getFluid(), 8000, EnumDyeColor.PURPLE, "Tank", new BoundingRectangle(58, 25, 18, 54));
    }

    @Override
    protected long consumeFuel() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        if (tank.getFluid() != null && tank.getFluidAmount() >= 1) {
            tank.drain(1, true);
            return generatedPower * 7L;
        }
        return 0;
    }

    @Override
    public long getEnergyFillRate() {
        return generatedPower;
    }

    @Override
    protected long getMaxEnergy() {
        return 1000000;
    }

    public abstract Fluid getFluid();
}
