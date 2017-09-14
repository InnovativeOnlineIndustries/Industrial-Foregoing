package com.buuz135.industrial.jei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class JEIHelper {

    public static boolean isInstalled() {
        return Loader.isModLoaded("jei");
    }

    public static void openBlockUses(ItemStack stack) {
        if (isInstalled()) JEICustomPlugin.showUses(stack);
    }
}
