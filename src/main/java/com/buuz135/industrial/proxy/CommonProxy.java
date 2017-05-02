package com.buuz135.industrial.proxy;

import java.util.Random;

public class CommonProxy {

    public static Random random;

    public void preInit() {
        random = new Random();
        FluidsRegistry.registerFluids();
        BlockRegistry.registerBlocks();
        ItemRegistry.registerItems();
    }

    public void init() {

    }

    public void postInit() {

    }
}
