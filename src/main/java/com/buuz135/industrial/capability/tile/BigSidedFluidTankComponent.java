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
package com.buuz135.industrial.capability.tile;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;

import java.util.Collections;
import java.util.List;

public abstract class BigSidedFluidTankComponent<T extends IComponentHarness> extends SidedFluidTankComponent<T> {

    public BigSidedFluidTankComponent(String name, int amount, int posX, int posY, int pos) {
        super(name, amount, posX, posY, pos);
    }


    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return Collections.emptyList();
    }

    @Override
    protected void onContentsChanged() {
        sync();
    }

    public abstract void sync();
}
