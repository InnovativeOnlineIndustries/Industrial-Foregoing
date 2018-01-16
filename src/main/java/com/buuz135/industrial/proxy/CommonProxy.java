package com.buuz135.industrial.proxy;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.entity.EntityPinkSlime;
import com.buuz135.industrial.gui.GuiHandler;
import com.buuz135.industrial.proxy.event.*;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.RecipeUtils;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.CraftTweakerHelper;
import com.buuz135.industrial.utils.apihandlers.PlantRecollectableRegistryHandler;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import com.buuz135.industrial.utils.apihandlers.json.ConfigurationConditionFactory;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.HashMap;
import java.util.Random;

public class CommonProxy {

    public static Random random;

    public static DamageSource custom = new DamageSource("if_custom");
    public static ResourceLocation PINK_SLIME_LOOT;

    public void preInit(FMLPreInitializationEvent event) {
        IFRegistries.poke();

        CraftingHelper.register(new ResourceLocation(Reference.MOD_ID, "configuration_value"), new ConfigurationConditionFactory());
        random = new Random();

        FluidsRegistry.registerFluids();
        BlockRegistry.poke();

        MinecraftForge.EVENT_BUS.register(new BlockRegistry());
        MinecraftForge.EVENT_BUS.register(new ItemRegistry());
        MinecraftForge.EVENT_BUS.register(new StrawRegistry());
        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());
        MinecraftForge.EVENT_BUS.register(new WorldTickHandler());
        MinecraftForge.EVENT_BUS.register(new PlantRecollectableRegistryHandler());
        MinecraftForge.EVENT_BUS.register(new FakePlayerRideEntityHandler());
        MinecraftForge.EVENT_BUS.register(new PlantInteractorHarvestDropsHandler());

        NetworkRegistry.INSTANCE.registerGuiHandler(IndustrialForegoing.instance, new GuiHandler());

        CustomConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());
        CustomConfiguration.config.load();
        CustomConfiguration.sync();
        CustomConfiguration.configValues = new HashMap<>();
        CustomConfiguration.configValues.put("useTEFrames", CustomConfiguration.config.getBoolean("useTEFrames", Configuration.CATEGORY_GENERAL, true, "Use Thermal Expansion Machine Frames instead of Tesla Core Lib"));

        if (Loader.isModLoaded("crafttweaker")) CraftTweakerHelper.register();

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "pink_slime"), EntityPinkSlime.class, "pink_slime", 135135, IndustrialForegoing.instance, 32, 1, false, 10485860, 16777215);
        PINK_SLIME_LOOT = LootTableList.register(new ResourceLocation(Reference.MOD_ID, "entities/pink_slime"));
    }

    public void init() {
        RecipeHandlers.loadBioReactorEntries();
        RecipeHandlers.loadLaserLensEntries();
        RecipeHandlers.loadSludgeRefinerEntries();
        RecipeHandlers.loadProteinReactorEntries();
    }

    public void postInit() {
        CraftingUtils.generateCrushedRecipes();
        BlockRegistry.createRecipes();
        RecipeUtils.generateConstants();
    }
}
