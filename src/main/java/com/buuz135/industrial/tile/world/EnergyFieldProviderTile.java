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
package com.buuz135.industrial.tile.world;

import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import net.minecraft.util.math.AxisAlignedBB;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;

public class EnergyFieldProviderTile extends WorkingAreaElectricMachine {

    public EnergyFieldProviderTile() {
        super(ElectricTileEntity.class.getName().hashCode());
    }

    @Override
    public float work() {
        return 0;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {//(getRadius()*getRadius())/3 -1, (getRadius()*getRadius())/3 -1, (getRadius()*getRadius())/3 -1
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(getRadius() * 2 + 3, getRadius() * 2 + 3, getRadius() * 2 + 3);
    }

    public float consumeWorkEnergy(long consume) {
        return this.getEnergyStorage().takePower(consume);
    }
}
