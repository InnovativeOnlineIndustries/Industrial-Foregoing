package com.buuz135.industrial.proxy;

import com.buuz135.industrial.config.CustomConfiguration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public static DamageSource custom = new DamageSource("if_custom");

    public void preInit(FMLPreInitializationEvent event) {
        random = new Random();
        FluidsRegistry.registerFluids();
        ItemRegistry.registerItems();
        BlockRegistry.registerBlocks();

        GameRegistry.addSmelting(UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, FluidsRegistry.LATEX), new ItemStack(ItemRegistry.plastic), 0);
        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
        MinecraftForge.EVENT_BUS.register(new MobDeathHandler());

        CustomConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());
        CustomConfiguration.sync();
    }

    public void init() {

    }

    public void postInit() {

    }
}
