package com.buuz135.industrial.item.infinity.item;

import com.buuz135.industrial.item.infinity.ItemInfinity;
import com.buuz135.industrial.module.ModuleTool;

public class ItemInfinityBackpack extends ItemInfinity {

    public static int POWER_CONSUMPTION = 10000;
    public static int FUEL_CONSUMPTION = 30;

    public ItemInfinityBackpack() {
        super("infinity_backpack", ModuleTool.TAB_TOOL, new Properties().maxStackSize(1), POWER_CONSUMPTION, FUEL_CONSUMPTION, false);
    }


}
