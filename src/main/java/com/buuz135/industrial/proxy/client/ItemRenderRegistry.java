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
        fertilizer.registerRender();
        laserLensItem.registerRender();
        laserLensItem_inverted.registerRender();
        pinkSlime.registerRender();

        adultFilterAddomItem.registerRenderer();
        rangeAddonItem.registerRenderer();
    }
}
