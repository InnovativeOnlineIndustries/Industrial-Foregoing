package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.MeatFeederItem;
import com.buuz135.industrial.item.MobImprisonmentToolItem;

public class ItemRegistry {

    public static MeatFeederItem meatFeederItem;
    public static MobImprisonmentToolItem mobImprisonmentToolItem;

    public static void registerItems() {
        (meatFeederItem = new MeatFeederItem()).register();
        (mobImprisonmentToolItem = new MobImprisonmentToolItem()).register();
    }
}
