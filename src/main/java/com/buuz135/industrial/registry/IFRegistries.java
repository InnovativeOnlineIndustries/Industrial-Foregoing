/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.registry;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class IFRegistries {

    public static Supplier<IForgeRegistry<StrawHandler>> STRAW_HANDLER_REGISTRY;

    public static Supplier<IForgeRegistry<PlantRecollectable>> PLANT_RECOLLECTABLES_REGISTRY;

    public static void create(NewRegistryEvent newRegistryEvent) {
        STRAW_HANDLER_REGISTRY = newRegistryEvent.create(new RegistryBuilder<StrawHandler>()
                .setName(new ResourceLocation(Reference.MOD_ID, "straw"))
                .setIDRange(1, Integer.MAX_VALUE - 1)
                .setType(StrawHandler.class)
                .disableSaving());
        PLANT_RECOLLECTABLES_REGISTRY = newRegistryEvent.create(new RegistryBuilder<PlantRecollectable>()
                .setName(new ResourceLocation(Reference.MOD_ID, "plant_recollectable"))
                .setIDRange(1, Integer.MAX_VALUE - 1)
                .setType(PlantRecollectable.class)
                .disableSaving());
    }
}