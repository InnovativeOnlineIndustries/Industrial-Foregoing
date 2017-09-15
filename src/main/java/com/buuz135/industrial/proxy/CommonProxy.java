package com.buuz135.industrial.proxy;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.config.CustomConfiguration;
import com.buuz135.industrial.entity.EntityPinkSlime;
import com.buuz135.industrial.proxy.event.MeatFeederTickHandler;
import com.buuz135.industrial.proxy.event.MobDeathHandler;
import com.buuz135.industrial.proxy.event.WorldTickHandler;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.CraftingUtils;
import com.buuz135.industrial.utils.RecipeUtils;
import com.buuz135.industrial.utils.Reference;
import com.buuz135.industrial.utils.apihandlers.CraftTweakerHelper;
import com.buuz135.industrial.utils.apihandlers.RecipeHandlers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public static DamageSource custom = new DamageSource("if_custom");
    public static ResourceLocation PINK_SLIME_LOOT;

    public void preInit(FMLPreInitializationEvent event) {
        IFRegistries.poke();
        random = new Random();

        FluidsRegistry.registerFluids();

        MinecraftForge.EVENT_BUS.register(new BlockRegistry());
        MinecraftForge.EVENT_BUS.register(new ItemRegistry());
        MinecraftForge.EVENT_BUS.register(new StrawRegistry());
        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());
        MinecraftForge.EVENT_BUS.register(new WorldTickHandler());

        CustomConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());

        if (Loader.isModLoaded("crafttweaker")) CraftTweakerHelper.register();

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "pink_slime"), EntityPinkSlime.class, "pink_slime", 135135, IndustrialForegoing.instance, 32, 1, false, 10485860, 16777215);
        PINK_SLIME_LOOT = LootTableList.register(new ResourceLocation(Reference.MOD_ID, "entities/pink_slime"));
    }

    public void init() {
        RecipeHandlers.registerRecollectables();
        RecipeHandlers.loadBioReactorEntries();
        RecipeHandlers.loadLaserLensEntries();
        RecipeHandlers.loadSludgeRefinerEntries();

    }

    public void postInit() {
        CustomConfiguration.sync();
        CraftingUtils.generateCrushedRecipes();
        BlockRegistry.createRecipes();
        RecipeUtils.generateConstants();
    }
}
