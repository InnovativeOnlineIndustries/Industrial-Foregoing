package com.buuz135.industrial.item.addon;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.utils.Reference;
import net.ndrei.teslacorelib.items.BaseAddon;

public abstract class CustomAddon extends BaseAddon {

    protected CustomAddon(String registryName) {
        super(Reference.MOD_ID, IndustrialForegoing.creativeTab, registryName);
    }

    public abstract void createRecipe();
}
