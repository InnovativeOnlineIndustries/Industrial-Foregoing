package com.buuz135.industrial.config.item.core;

import com.buuz135.industrial.config.ItemCoreConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile.Child(ItemCoreConfig.class)
public class MobImprisonmentToolConfig {

    @ConfigVal(comment = "If true, only the player who tamed the animal can capture it.")
    public static boolean onlyOwnerCanCapture = false;
}
