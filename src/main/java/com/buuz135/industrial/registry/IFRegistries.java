package com.buuz135.industrial.registry;

import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.straw.StrawHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class IFRegistries {

    public static final ForgeRegistry<StrawHandler> STRAW_HANDLER_REGISTRY = (ForgeRegistry<StrawHandler>) new RegistryBuilder<StrawHandler>()
            .setName(new ResourceLocation(IndustrialForegoingHelper.MOD_ID + ":straw"))
            .setIDRange(1, Integer.MAX_VALUE - 1)
            .setType(StrawHandler.class)
            .create();

    public static void poke() {}
}