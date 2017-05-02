package com.buuz135.industrial.proxy;

import com.buuz135.industrial.item.MeatFeederItem;

public class ItemRegistry {

    public static MeatFeederItem meatFeederItem;

    public static void registerItems() {
        (meatFeederItem = new MeatFeederItem()).register();
    }
}
