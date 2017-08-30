package com.buuz135.industrial.utils.apihandlers;

import com.buuz135.industrial.utils.apihandlers.crafttweaker.CTBioReactor;
import crafttweaker.CraftTweakerAPI;

public class CraftTweakerHelper {

    public static void register() {
        CraftTweakerAPI.registerClass(CTBioReactor.class);
    }
}
