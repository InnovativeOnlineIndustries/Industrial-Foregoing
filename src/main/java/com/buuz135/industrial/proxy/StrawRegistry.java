/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.proxy;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.apihandlers.straw.*;
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
        registry.register(new PotionStrawHandler(FluidsRegistry.PROTEIN)
                .addPotion(MobEffects.ABSORPTION, 100, 3)
                .addPotion(MobEffects.SATURATION, 300, 3)
                .setRegistryName("protein"));
        registry.register(new PotionStrawHandler(FluidsRegistry.LATEX)
                .addPotion(MobEffects.POISON, 1000, 2)
                .addPotion(MobEffects.SLOWNESS, 1000, 2)
                .setRegistryName("latex"));
    }
}