package com.buuz135.industrial.utils.apihandlers;

import com.buuz135.industrial.utils.apihandlers.crafttweaker.*;
import crafttweaker.CraftTweakerAPI;

public class CraftTweakerHelper {

    public static void register() {
        CraftTweakerAPI.registerClass(CTBioReactor.class);
        CraftTweakerAPI.registerClass(CTLaserDrill.class);
        CraftTweakerAPI.registerClass(CTSludgeRefiner.class);
        CraftTweakerAPI.registerClass(CTProteinReactor.class);
        CraftTweakerAPI.registerClass(CTFluidDictionary.class);
    }
}
