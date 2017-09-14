package com.buuz135.industrial.proxy;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.strawhandlers.*;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;


public class StrawRegistry {

    @SubscribeEvent
    public void register(RegistryEvent.Register<StrawHandler> event) {
        IForgeRegistry<StrawHandler> registry = event.getRegistry();
        registry.registerAll(new WaterStrawHandler(), new LavaStrawHandler(), new MilkStrawHandler(), new EssenceStrawHandler());
        registry.register(new PotionStrawHandler(FluidsRegistry.BIOFUEL)
                .addPotion(MobEffects.SPEED, 800, 0)
                .addPotion(MobEffects.HASTE, 800, 0)
                .setRegistryName("biofuel"));
        registry.register(new PotionStrawHandler(FluidsRegistry.SLUDGE)
                .addPotion(MobEffects.WITHER, 600, 0)
                .addPotion(MobEffects.BLINDNESS, 1000, 0)
                .addPotion(MobEffects.SLOWNESS, 1200, 1)
                .setRegistryName("sludge"));
        registry.register(new PotionStrawHandler(FluidsRegistry.SEWAGE)
                .addPotion(MobEffects.NAUSEA, 1200, 0)
                .addPotion(MobEffects.SLOWNESS, 1200, 0)
                .setRegistryName("sewage"));
        registry.register(new PotionStrawHandler(FluidsRegistry.MEAT)
                .addPotion(MobEffects.ABSORPTION, 100, 2)
                .addPotion(MobEffects.SATURATION, 300, 2)
                .setRegistryName("meat"));
        registry.register(new PotionStrawHandler(FluidsRegistry.LATEX)
                .addPotion(MobEffects.POISON, 1000, 2)
                .addPotion(MobEffects.SLOWNESS, 1000, 2)
                .setRegistryName("latex"));
    }
}