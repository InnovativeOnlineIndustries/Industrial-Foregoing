package com.buuz135.industrial.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public void preInit() {
        random = new Random();
        FluidsRegistry.registerFluids();
        BlockRegistry.registerBlocks();
        ItemRegistry.registerItems();

        MinecraftForge.EVENT_BUS.register(new MeatFeederTickHandler());
    }

    public void init() {

    }

    public void postInit() {

    }
}
