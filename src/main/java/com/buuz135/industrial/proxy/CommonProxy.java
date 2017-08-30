package com.buuz135.industrial.proxy;

import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.RecipeUtils;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public static DamageSource custom = new DamageSource("if_custom");

    public void preInit(FMLPreInitializationEvent event) {
        random = new Random();

        FluidsRegistry.registerFluids();
        RecipeHandlers.loadBioReactorEntries();
        RecipeHandlers.loadLaserLensEntries();

        MinecraftForge.EVENT_BUS.register(new BlockRegistry());
        MinecraftForge.EVENT_BUS.register(new ItemRegistry());
        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());

        CustomConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());

    }

    public void init() {

    }

    public void postInit() {
        CustomConfiguration.sync();
        CraftingUtils.generateCrushedRecipes();
        BlockRegistry.createRecipes();
        RecipeUtils.generateConstants();
    }
}
