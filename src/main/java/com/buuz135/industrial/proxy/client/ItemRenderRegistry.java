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
package com.buuz135.industrial.proxy.client;

import static com.buuz135.industrial.proxy.ItemRegistry.*;

public class ItemRenderRegistry {

    public static void registerRender() {
        meatFeederItem.registerRender();
        mobImprisonmentToolItem.registerRender();
        tinyDryRubber.registerRender();
        dryRubber.registerRender();
        plastic.registerRender();
        strawItem.registerRender();
        conveyorUpgradeItem.registerRender();
        fertilizer.registerRender();
        laserLensItem.registerRender();
        laserLensItem_inverted.registerRender();
        pinkSlime.registerRender();
        pinkSlimeIngot.registerRender();
        energyFieldAddon.registerRenderer();
        bookManualItem.registerRender();
        itemInfinityDrill.registerRender();
        if (artificalDye != null) artificalDye.registerRender();

        adultFilterAddomItem.registerRenderer();
        rangeAddonItem.registerRenderer();
        leafShearingAddonItem.registerRenderer();

        itemStackTransferAddonPull.registerRenderer();
        itemStackTransferAddonPush.registerRenderer();
        fluidTransferAddonPull.registerRenderer();
        fluidTransferAddonPush.registerRenderer();
        fortuneAddonItem.registerRenderer();
    }
}
